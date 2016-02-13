//
//  main.c
//  pa3
//
//  Created by Joseph DeVita on 3/10/15.
//  Copyright (c) 2015 Joseph DeVita. All rights reserved.
//

#include "string.h"
#include <stdio.h>
#include "tokenizer2.h"
#include "hash.h"
#include "uthash.h"
#include "ftw.h"

struct indexhash **h_table;

int indexer(const char *filename, const struct stat *status, int type);

void hash_tokens(char *input_file);

void print_JSON();

int main(int argc, const char * argv[]) {
    // insert code here...
    
    if (argc != 3) {
        printf("INVALID INPUT: please enter an output and a valid source file\n");
        return 2;
    }
    
    FILE *file = fopen(argv[2], "r");
    
    h_table = NULL;
    
    h_table = malloc(sizeof(struct indexhash));
    
    ftw(argv[2], indexer, 100);
    
    print_JSON();
    
    return 0;
}

int indexer(const char *filename, const struct stat *status, int type){
    
    char file_name[2048];
    strcpy(file_name, filename);
    
    if (type == FTW_NS)
    {
        return 0;
    }
    else if (type == FTW_F)
    {
        hash_tokens(file_name); // call tokenizer
    }
    
    if (type == FTW_D && strcmp(".", filename) != 0) {
        
    }
    
    return 0;

    
}

void hash_tokens(char *input_file){
    
    char * tokenstream = 0;
    long length;
    FILE * f = fopen(input_file, "rb");
    
    if(f)
    {
        fseek(f, 0, SEEK_END);
        length = ftell(f);
        fseek(f, 0, SEEK_SET);
        tokenstream = malloc(length) + 1;
        if (tokenstream) {
            fread(tokenstream, 1, length, f);
        }
        fclose(f);
    }
    
    tokenstream[strlen(tokenstream)-1] = '\0';
    
    printf("%s\n",tokenstream);
    
    if (tokenstream) {
        
        TokenizerT *tk = TKCreate(tokenstream);
    
        while (tk->current_position - tk->copied_string < strlen(tk->copied_string)) {
        
            addtoken(h_table, 1, TKGetNextToken(tk), input_file);
        
        }
        
        struct indexhash *s;
    
        for(s = *h_table; s != NULL; s=s->hh.next) {
            printf("token: %s --- %d\n", s->token, s->sh->count);
        }
    }
}

void print_JSON()
{
    FILE *fp = fopen("output.txt", "w+");
    struct indexhash *s = *h_table;
    struct subhash *sh;
    
    HASH_SORT(s, token_sort);
    
    fprintf(fp, "{\"list\" : [\n");
    for(; s != NULL; s=s->hh.next)
    {
        fprintf(fp, "\t{\"%s\" : [\n",s->token);
        
        for (sh = s->sh; sh != NULL; sh=sh->hh.next)
        {
            fprintf(fp, "\t\t{\"%s\" : %d},\n",sh->fn,sh->count);
        }
        fprintf(fp, "\t]},\n");
    }
    fprintf(fp,"]}");
}