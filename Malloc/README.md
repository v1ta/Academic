---
author:
- a big ass bear
title: Dynamic Memory Allocation Functions README
...

This application intends to re-implement the standard GNU functions:

    void * malloc(size_t);

    void free(void *);

Malloc 
======

The implementation of void \* malloc(size<span>\_</span>t) focuses on
providing a storage scheme in which large allocations grow from one end
of a heap, and smaller allocations grow from the opposing end. This is
accomplished by creating pre-defined bucket sizes for requested
allocations \<= these sizes. The bucket sizes are powers of 2 with the
intent of being able to easily re-use buckets for subsequent allocations
after calls to free(). Bucket sizes are system-architecture dependent:

      2\textsuperscrupt{4}
                Bytes:
                32-bit          64-bit
        4   16      -
        5   32      32
        6   64      64
        7   128     128
        8   256     256
        9   512     512
        10  -       1024

Upon an initial call to malloc, a call to void \*
sbrk(intptr<span>\_</span>t increment) will request ’increment’ new
memory. The default heap request is 2^13^ (8192). Two head, and two tail
pointers will be created for both doubly linked lists; The two head
pointers will provide a means of bounds checking for the entire heap
page:

    typedef struct heap
    {
        unsigned int in_use;  
        Block *root, *last;
        Block *unfixed_tail, *fixed_tail;
    } Heap;

Individual memory segments will be cataloged via an reference object
which is prepended to each allocated segmented:

    typedef struct block
    {
        struct block *prev, *next;
        int isfree; 
        size_t size;
    } Block;

A variable tracks the current usage of the heap. This allows for early
pages to only be re-traversed if their is the possibility of an
allocation fitting a heap which may have previously been filled. Due
note this doesn’t guarantee a given allocation n \<= (Heap -
in<span>\_</span>use) will actually be allocatable into said heap.

Test-Cases
==========

    TC 1: (Basic un-fixed bucket add, char *)
    : mutable string w/no seg-fault
    : appended to start of heap, growing toward end
    TC 2: (Basic fixed bucket add, char *)
    : mutable string w/no seg-fault
    : appended to end of heap, growing toward start
    TC 3: (Alignment test, corrupt tail via head using fixed address)
    : tail string prints out corrupted data
    : usage of char[address] works via fixed value
