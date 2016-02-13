#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "tokenizer2.h"

TokenizerT *TKCreate(char *ts) 
{
	if(ts == NULL){
		return NULL;
	}
	
	TokenizerT *tokenizer = (TokenizerT *)malloc(sizeof(TokenizerT));
	
	if(tokenizer == NULL){
		return NULL;
	}
	
	tokenizer->copied_string = ts;
	tokenizer->current_position = tokenizer->copied_string;
	
	return tokenizer;
}

char is_delimiter(char character) 
{
	if(isalnum(character))
	{	
		return 0;
	}
	return 1;
}

char *TKGetNextToken(TokenizerT *tk) 
{
	char *token = NULL;
	char *token_start = NULL;

	while(tk->current_position - tk->copied_string < strlen(tk->copied_string)) 
	{
		if(!is_delimiter(*tk->current_position)) 
		{
			token_start = tk->current_position;
			break;
		}
		tk->current_position++;
	}
	
	if(token_start == NULL) 
	{
		return NULL;
	}
	
	while(tk->current_position - tk->copied_string < strlen(tk->copied_string)) 
	{
		if(is_delimiter(*tk->current_position)) 
		{
			break;
		}
		tk->current_position++;
	}	

	token = (char *)malloc(sizeof(char) * (tk->current_position - token_start + 1));
	strncpy(token, token_start, tk->current_position - token_start);
	token[(tk->current_position - token_start)] = '\0';
	
	return token;
}

void TKDestroy(TokenizerT *tk) 
{	
	free(tk);
}

