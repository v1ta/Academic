//
//  main.c
//  SortedList
//
//  Created by Joseph DeVita on 2/21/15.
//  Copyright (c) 2015 Joseph DeVita. All rights reserved.
//

#include "sorted-list.h"
#include "stdlib.h"
#include "stdio.h"
#include "string.h"

struct Object_small{
    int x;
};

typedef struct Object_small Object_small;

struct Object_medium{
    int x;
    int y;
    int z;
    int a;
};

typedef struct Object_medium Object_medium;

struct Object_large{
    double x;
    double y;
    double z;
    double a;
    char * str;
    double f;
};

typedef struct Object_large Object_large;

struct Object_extra_large{
    double a;
    double b;
    double c;
    double d;
    double e;
    double f;
    double g;
    double h;
    double i;
    double j;
};

typedef struct Object_extra_large Object_extra_large;


int comparator(void *o1, void *o2);

void destructor(void *list);


int main(int argc, const char * argv[]) {
    
    char * str0 = (char *)calloc(sizeof(char), 50);
    char * str1 = (char *)calloc(sizeof(char), 100);
    char * str2 = (char *)calloc(sizeof(char), 200);
    char * str3 = (char *)calloc(sizeof(char), 300);
    char * str4 = (char *)calloc(sizeof(char), 400);
    char * str5 = (char *)calloc(sizeof(char), 100);
    
    strcpy(str0, "supar short");
    strcpy(str1, "this is a short string");
    strcpy(str2, "this is a longer string so I can test the comparator");
    strcpy(str3, "this is an even longer string so I can test the comparator");
    strcpy(str4, "this is a super duper mooper long string so I can test this stuff");
    strcpy(str5, "this is a short stringmk");
    
    SortedListPtr list = SLCreate(comparator, destructor);
    
    SLInsert(list, &str1);
    
    SLInsert(list, &str3);
    
    SLInsert(list, &str0);
    
    SLInsert(list, &str4);
    
    SLInsert(list, &str5);
    
   // SLInsert(list, &str2);
    
    SortedListIteratorPtr iter = SLCreateIterator(list);
    
    Node curr = NULL;
    
    printf("%d\n",list->nodes);
    
    //SLRemove(list, &str0);
    //SLRemove(list, &str4);
    //SLRemove(list, &str2);
    
    //65 58 52 22 22 11
   // printf("%d\n",list->nodes);
    
    printf("testing iter GET: %lu\n",strlen(*((char**)SLGetItem(iter)))); //65

 
    printf("testing iter NEXT: %lu\n",strlen(*((char**)SLNextItem(iter)))); //58
    SLInsert(list, &str4);
    printf("testing iter NEXT: %lu\n",strlen(*((char**)SLNextItem(iter)))); //52
    printf("testing iter NEXT: %lu\n",strlen(*((char**)SLNextItem(iter)))); //22
    
    

    SLDestroyIterator(iter);
    
    for(curr = list->next; curr != NULL; curr = curr->next)
    {
        printf("output strlen: %lu\n", strlen(*((char**)curr->Object)));
        printf("output refcount: %d\n", curr->ref_count);
    }
    
    SLDestroy(list);
    
    return 0;

}

int comparator(void *o1, void *o2)
{
    //using strings for ease of testing
    return (int)strlen(*((char**)o1)) - (int)strlen(*((char**)o2));
}

void destructor(void *list){
    
}


