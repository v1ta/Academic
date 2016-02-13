//Jessica Calabretta, Daoun Oh
#ifndef TOKENIZER2_H
#define TOKENIZER2_H


struct TokenizerT_ {
	char *copied_string;			
	char *current_position;
};
typedef struct TokenizerT_ TokenizerT;

/* Function: TKCreate
 * Description: creates a new tokenizer struct from the token stream 
 * Parameters: token stream
 * Modifies: nothing
 * Returns: a pointer to a tokenizer struct on success, a null pointer on failure
 */
TokenizerT *TKCreate(char *ts);

/* Function: is_delimiter
 * Description: determines if a particular character is alphanumeric
 * Parameters: character to be compared
 * Modifies: Nothing
 * Returns: 1 if character is not alphanumeric, 0 if it is 
 */
char is_delimiter(char character);

/* Function: TKGetNextToken
 * Description: returns the next token from the token stream specified within the tokenizer
 * Parameters: tokenizer from which to extract token
 * Modifies: tokenizer->current_position: identifies starting point of next token; creates a new string with 
 * Returns: token extracted as a char* on success, null on failure/end of string;
 */
char *TKGetNextToken(TokenizerT *tk);

/* Function: TKDestroy
 * Description: destroys tokenizer struct and deallocates all memory
 * Parameters: tokenizer to be destroyed
 * Modifies: tokenizer struct: deallocates memory
 * Returns: nothing 
 */
void TKDestroy(TokenizerT *tk);
	
#endif	
	
