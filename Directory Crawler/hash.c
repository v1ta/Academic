//Jessica Calabretta, Daoun Oh
#include <stdio.h>
#include "hash.h"

int addtoken(struct indexhash **tokes, int count, char *token, char *fn)
{
	struct indexhash *h;
	struct subhash *tmp;
	struct indexhash *hash;
	struct subhash *sh;
	
	HASH_FIND_STR(*tokes, token, h);	
	if( h == NULL)//if the token has not been inserted 
	{
		//insert the token			
		hash = malloc(sizeof(struct indexhash));
		hash->sh=NULL;
		strcpy(hash->token, token);
		HASH_ADD_STR(*tokes,token,hash);
		
		//insert the subhash containing the filename and count
		sh = malloc(sizeof(struct subhash));
		sh->count=count;
		strcpy(sh->fn, fn);
		HASH_ADD_STR(hash->sh,fn,sh);
		
		return 0;
	}
	else
	{
        //updatetoken(tokes, token, fn);
		HASH_FIND_STR(h->sh, fn, tmp);
		if(tmp == NULL)//if the token has been inserted into the hash table 
		{				//but the filename has not been inserted into the subhash of that token
			//insert the filename and count in the subhash of the token
			sh = malloc(sizeof(struct subhash));
			sh->count = count;
			strcpy(sh->fn, fn);
			HASH_ADD_STR(h->sh,fn,sh);
			return 0;
		}
	}
	return 1;
}

int updatetoken(struct indexhash **tokes, char *token, char *fn)
{
	struct indexhash *h;
	struct subhash *sh;
	HASH_FIND_STR(*tokes, token, h); //find token 
	HASH_FIND_STR(h->sh, fn, sh); //find subhash of token	
	sh->count++; //update count in the subhash
	return 0;
}

void destroy(struct indexhash **tokes)
{
	struct indexhash *h = *tokes, *tmp1;
	struct subhash *sh = h->sh, *tmp2;
	
	HASH_ITER(hh, *tokes, h, tmp1) 
	{
		HASH_ITER(hh, h->sh, sh, tmp2) 
		{
			HASH_DEL(h->sh, sh);
			free(sh);
		}
		HASH_DEL(*tokes, h);		
	}
	free(h);
	free(sh);
	free(tmp1);
	//free(tmp2);
}

int token_sort(struct indexhash *a, struct indexhash *b) 
{
    return strcmp(a->token,b->token);
}

int fn_sort(struct subhash *a, struct subhash *b) 
{
    return strcmp(a->fn,b->fn);
}

int count_sort(struct subhash *a, struct subhash *b) 
{
    
    if (a->count < b->count)
	return (int) 1;
	
	if (a->count == b->count)
	return (int)  0;
	
	else // (a->count > b->count)
	return (int)  -1;   
}
