//Jessica Calabretta, Daoun Oh
#include <stdio.h>
#include "uthash.h"

struct indexhash
{	
	char token[50];	
	UT_hash_handle hh;
	struct subhash *sh;	
};

struct subhash
{
	int count;
	char fn[50];
	UT_hash_handle hh;	
};

/* Function: addtoken
 * Description: Inserts token into the hashtable. 
 * 				Also inserts the filename and count values to subtable
 * Parameters: struct indexhash **, int, char *, char *
 * Modifies: hashtable
 * Returns: 0 if the token is inserted or if a new filename is inserted into its subhash
 * 			1 if the token already exists and the filename already exists in its subhash
 */
int addtoken(struct indexhash **tokes, int count, char *token, char *fn);

/* Function: updatetoken
 * Description: When there is more than one occurrance of the same token
 * 				in the same file, then this function updates the count
 * 				value by 1.
 * Parameters: struct indexhash **, char *, char *
 * Modifies: hashtable
 * Returns: 0 
 */
int updatetoken(struct indexhash **tokes, char *token, char *fn);

/* Function: destroy
 * Description: Frees all of the allocated space for the hash table.
 * Parameters: struct indexhash **
 * Modifies: hashtable
 * Returns: nothing
 */
void destroy(struct indexhash **tokes);

/* Function: token_sort
 * Description: sort the tokens in ascending order
 * Parameters: struct indexhash *, struct indexhash *
 * Modifies: hashtable
 * Returns: < 0, if a is less than b
 * 			0, if a is equal to b
 * 			> 0, if a is greater than b
 */
int token_sort(struct indexhash *a, struct indexhash *b); 

/* Function: fn_sort
 * Description: sort the token's subhash in ascending order based on filenames
 * Parameters: struct indexhash *, struct indexhash *
 * Modifies: hashtable
 * Returns: < 0, if a is less than b
 * 			0, if a is equal to b
 * 			> 0, if a is greater than b
 */
int fn_sort(struct subhash *a, struct subhash *b);

/* Function: count_sort
 * Description: sort the token's subhash in descending order based on counts
 * Parameters: struct indexhash *, struct indexhash *
 * Modifies: hashtable
 * Returns: -1, if a is less than b
 * 			0, if a is equal to b
 * 			1, if a is greater than b
 */
int count_sort(struct subhash *a, struct subhash *b);
