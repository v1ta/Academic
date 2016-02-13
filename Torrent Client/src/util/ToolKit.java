/*
 *  RUBTClient is a BitTorrent client written at Rutgers University for 
 *  instructional use.
 *  Copyright (C) 2008  Robert Moore II
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package util;

import model.TorrentClient;

import java.util.*;
import java.nio.*;
import java.io.*;
/**
 * Contains a number of methods useful for debugging and developing programs
 * using Bencoding methods.&nbsp; This class is specifically designed to be used
 * with <code>edu.rutgers.cs.cs352.bt.util.Bencoder</code> and
 * <code>Bencoder2</code>.
 * 
 * @author Robert Moore II
 * 
 */
public class ToolKit
{
    /**
     * Prints out Java versions of Bencoding data types.
     * @param o the Object to print.&nbsp; It must be one of the following
     * types: @
     */
    @SuppressWarnings("unchecked")
	public static void print(Object o)
    {
        if (o instanceof ByteBuffer)
            printString((ByteBuffer) o, true, 0);
        else if (o instanceof byte[])
            printString((byte[]) o, true, 0);
        else if (o instanceof Integer)
            printInteger((Integer) o, 0);
        else if (o instanceof ArrayList)
            printList((ArrayList) o, 0);
        else if (o instanceof HashMap)
            printMap((HashMap) o, 0);
        else
            System.err.println("Error: Unknown type");

    }

    /**
     * Prints the specified object with the provided depth.  The depth simply indicates how much to indent.
     * @param o the object to print
     * @param depth the depth of the object within another data type.
     */
    @SuppressWarnings("unchecked")
	public static void print(Object o, int depth)
    {
        if (o instanceof ByteBuffer)
            printString((ByteBuffer) o, true, depth + 1);
        else if (o instanceof byte[])
            printString((byte[]) o, true, depth + 1);
        else if (o instanceof Integer)
            printInteger((Integer) o, depth + 1);
        else if (o instanceof ArrayList)
            printList((ArrayList) o, depth + 1);
        else if (o instanceof HashMap)
            printMap((HashMap) o, depth + 1);
        else
            System.err.println("Error: Unknown type");
    }

    /**
     * Helper method that prints out a byte string as a series of integer or ASCII
     * characters.
     * @param bytes the byte string to print.
     * @param as_text {@code true} if the byte string should be printed as ASCII characters
     * @param depth the depth of the object within other objects, used for indenting.
     */
    public static void printString(byte[] bytes, boolean as_text, int depth)
    {
        for (int k = 0; k < depth; k++)
            System.out.print("  ");
        System.out.print("String: ");
        for (int i = 0; i < bytes.length; i++)
        {
            System.out
                    .print(as_text ? (char) bytes[i] : (int) bytes[i] + " ");
        }
        System.out.println();
    }

    /**
     * Helper method that prints out a {@code ByteBuffer} as a series of integer or ASCII
     * characters.
     * @param byte_string the {@code ByteBuffer} to print.
     * @param as_text {@code true} if the {@code ByteBuffer} should be printed as ASCII characters
     * @param depth the depth of the object within other objects, used for indenting.
     */
    public static void printString(ByteBuffer byte_string, boolean as_text,
            int depth)
    {
        for (int k = 0; k < depth; k++)
            System.out.print("  ");
        System.out.print("String: ");
        byte[] bytes = byte_string.array();
        for (int i = 0; i < bytes.length; i++)
        {
            System.out
                    .print(as_text ? (char) bytes[i] : (int) bytes[i] + " ");
        }
        System.out.println();
    }

    /**
     * Helper method that prints out an integer.
     * @param i the integer to print.
     * @param depth the depth of the object within other objects, used for indenting.
     */
    public static void printInteger(Integer i, int depth)
    {
        for (int k = 0; k < depth; k++)
            System.out.print("  ");
        System.out.println("Integer: " + i);
    }

    /**
     * Helper method that prints out a list.
     * @param list the list to print.
     * @param depth the depth of the object within other objects, used for indenting.
     */
    @SuppressWarnings("unchecked")
	public static void printList(AbstractList list, int depth)
    {
        final Iterator i = list.iterator();
        Object o = null;
        for (int k = 0; k < depth; k++)
            System.out.print("  ");
        System.out.println("List: ");
        while (i.hasNext() && (o = i.next()) != null)
        {
            for (int k = 0; k < depth; k++)
                System.out.print("  ");
            System.out.print(" +");
            print(o, depth);
        }
    }

    public static boolean[] checkPieces(TorrentInfo torrentInfo, File outputFile) throws IOException {

        int numPieces = torrentInfo.piece_hashes.length;
        int pieceLength = torrentInfo.piece_length;
        int fileLength = torrentInfo.file_length;
        ByteBuffer[] pieceHashes = torrentInfo.piece_hashes;
        int lastPieceLength = fileLength % pieceLength == 0 ? pieceLength : fileLength % pieceLength;

        byte[] piece = null;
        boolean[] verifiedPieces = new boolean[numPieces];

        for (int i = 0; i < numPieces; i++) {
            if (i != numPieces - 1) {
                piece = new byte[pieceLength];
                piece = readFile(i, 0, pieceLength, torrentInfo, outputFile);
            } else {
                piece = new byte[lastPieceLength];
                piece = readFile(i, 0, lastPieceLength, torrentInfo, outputFile);
            }



            if (TorrentClient.verifySHA1(piece, pieceHashes[i], i)) {
                verifiedPieces[i] = true;
            }
        }

        return verifiedPieces;
    }

    /**
     * Helper method that prints out a dictionary/map.
     * @param map the dictionary/map to print.
     * @param depth the depth of the object within other objects, used for indenting.
     */
    @SuppressWarnings("unchecked")
	public static void printMap(Map map, int depth)
    {
        final Iterator i = map.keySet().iterator();
        Object key = null;
        for (int k = 0; k < depth; k++)
            System.out.print("  ");
        System.out.println("Dictionary:");
        while (i.hasNext() && (key = i.next()) != null)
        {
            for (int k = 0; k < depth; k++)
                System.out.print("  ");
            System.out.print("(K) ");
            print(key, depth);
            Object val = map.get(key);
            for (int k = 0; k < depth; k++)
                System.out.print("  ");
            System.out.print("(V) ");
            print(val, depth);
        }
    }

    public static byte[] readFile(int index, int offset, int length, TorrentInfo torrentInfo, File outputFile) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(outputFile, "r");
        byte[] data = new byte[length];

        raf.seek(torrentInfo.piece_length * index + offset);
        raf.read(data);
        raf.close();

        return data;
    }
    /*
     * Bencoding a method of encoding binary data. Tracker responses, and the metainfo file will be bencoded. 
     * Below is how data types are bencoded according to the BT protocol. 
     * [The following list is taken fromhttp://www.bittorrent.org/beps/bep_0003.html ]
    •    Strings are length-prefixed base-10 followed by a colon and the string. 
    They are encoded in UTF-8. For example 4:spam corresponds to ‘spam'.
    •    Integers are represented by an 'i' followed by the number in ASCII base-10 followed by an 'e'. 
    For example i3e corresponds to 3 and i-3e corresponds to -3. 
    Integers have no size limitation. i-0e is invalid. 
    All encodings with a leading zero, such as i03e, are invalid, other than i0e, which of course corresponds to 0.
    •    Lists are encoded as an 'l' followed by their elements (also bencoded) followed by an 'e'. 
    For example, l4:spam4:eggse corresponds to ['spam', ‘eggs'].
    •    Dictionaries are encoded as a 'd' followed by a list of alternating keys and their corresponding values followed by an 'e'. 
    For example, d3:cow3:moo4:spam4:eggse corresponds to {'cow':'moo', 'spam':'eggs'} and d4:spaml1:a1:bee corresponds to {'spam': ['a', 'b']}. 
    Keys must be strings and appear in sorted order (sorted as raw strings, not alphanumerics).
     */
}