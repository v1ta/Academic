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

int compareInts(void *p1, void *p2)
{
    int i1 = *(int*)p1;
    int i2 = *(int*)p2;
    
    return i1 - i2;
}


 int compareStrings(void *p1, void *p2)
 {
 char *s1 = p1;
 char *s2 = p2;

 return (int)strlen(s1) - (int)strlen(s2);
 }


void destroyAlloc(void*p){
    free(p);
}

void destroy(void*p){
    return;
}

struct StructT_
{
    int data;
};

typedef struct StructT_ Struct_Obj;

int compare_structs(void*p1 ,void*p2)
{
    Struct_Obj *s1 = p1;
    Struct_Obj *s2 = p2;
    
    return s1->data - s2->data;
}


int main(int argc, const char * argv[]) {
    
    char * strs[7] = {"supar short","this is a short string","this is a longer string so I can test the comparator", "this is an even longer string so I can test the comparator", "this is a super duper mooper long string so I can test this stuff", "this is a short stringmk","this is an even longer string so I can test the comparator"};
    
    int numbers[5] = {5,1,3,7,5};
    
    Struct_Obj strtarr[7];
    strtarr[0].data = 200;
    strtarr[1].data = 1;
    strtarr[2].data = 3;
    strtarr[3].data = 9;
    strtarr[4].data = 45;
    strtarr[5].data = 600;
    strtarr[6].data = 324;
    
    char c;
    int i;
    
    c = getchar();
    
    if (c == '1') {
        
        /*
         * This test is simply to test order, it will populat a list,
         * and then remove a few items. The iterator is absent from this test.
         */
        
        SortedListPtr list = SLCreate(compareStrings, destroy);
        
        
        
        for(i = 0; i < 3; i++)
            SLInsert(list, strs[i]);
        
        //SLRemove(list, strs[3]);
        //SLRemove(list, strs[5]);
        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            printf("output strlen: %lu\n", strlen(((char*)curr->Object)));
        }
        
        SLDestroy(list);
    }
    else if (c == '2')
    {
        /*
         * This will test iterators traversing a list.
         * A remove will be inserted mid traversal to test the "ghost routine"
         * described in the readme.
         */
        
        SortedListPtr list = SLCreate(compareStrings, destroy);
        
        
        for(i = 0; i < 7; i++)
            SLInsert(list, strs[i]);
        
        SortedListIteratorPtr iter2= SLCreateIterator(list); // this will work because the list has elements.
        
        SLGetItem(iter2);
        SLNextItem(iter2);
        SLNextItem(iter2);
        
        SLRemove(list, strs[1]);
        SLRemove(list, strs[4]);
        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            
            printf("output: %lu\n", strlen(((char*)curr->Object)));
        }
        
        SLDestroyIterator(iter2);
        SLDestroy(list);
    }
    else if(c == '3')
    {
        /*
         * This case will test the ghost node functionality of SortedList.
         * It will create some iterators and delete the object at the target location
         * of the current iterator.
         */
        
        SortedListPtr list = SLCreate(compareStrings, destroy);
        
        
        for(i = 0; i < 7; i++)
            SLInsert(list, strs[i]);
        
        SortedListIteratorPtr iter= SLCreateIterator(list);
        SLRemove(list, strs[4]); //remove head
        SLRemove(list, strs[3]); //remove head + 1
        SLRemove(list, strs[3]); //remove head + 2
        SLNextItem(iter);
        
        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            
            printf("output: %lu\n", strlen(((char*)curr->Object)));
        }
        
        SLDestroyIterator(iter);
        SLDestroy(list);
    }
    else if(c == '4')
    {
        /*
         * This test case will create and list, assign it two iterator
         * and remove the node of the current target of the iterator.
         * The node at the end of the list will be removed. We will then
         * try to push the iterator passed the end of the list.
         */
        
        SortedListPtr list = SLCreate(compareStrings, destroy);
        
        for(i = 0; i < 7; i++)
            SLInsert(list, strs[i]);
        
        SortedListIteratorPtr iter= SLCreateIterator(list);
        SortedListIteratorPtr iter2 = SLCreateIterator(list);
        
        SLRemove(list, strs[4]); //remove head
        SLRemove(list, strs[3]); //remove head + 1
        SLRemove(list, strs[3]); //remove head + 2
        SLRemove(list, strs[0]); //remove tail
        SLNextItem(iter2);
        SLNextItem(iter2);
        SLNextItem(iter2);
        SLNextItem(iter2);
        SLNextItem(iter2); //well passed end of list
        SLNextItem(iter);
        SLNextItem(iter);
        
        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            
            printf("output: %lu\n", strlen(((char*)curr->Object)));
        }
        
        SLDestroyIterator(iter);
        SLDestroyIterator(iter2);
        SLDestroy(list);
    }
    else if(c == '5')
    {
        /*
         * This is the stress test-case we will use sting objects, it will
         * test all of the previous cases along with actions to attempt to
         * weed out an edge case.
         */
        
        SortedListPtr list = SLCreate(compareStrings, destroy);
        
        for(i = 0; i < 7; i++)
            SLInsert(list, strs[i]);
        
        SortedListIteratorPtr iter= SLCreateIterator(list);
        SortedListIteratorPtr iter2 = SLCreateIterator(list);
        
        SLRemove(list, strs[4]); //remove head
        SLRemove(list, strs[3]); //remove head + 1
        SLRemove(list, strs[3]); //remove head + 2
        SLRemove(list, strs[0]); //remove tail
        SLNextItem(iter);
        SLNextItem(iter2);
        SLNextItem(iter2);
        SLNextItem(iter2);
        SLNextItem(iter2); //well passed end of list
        SLNextItem(iter);
        SLNextItem(iter);
        
        for(i = 0; i < 7; i++)
            SLInsert(list, strs[i]);
        
        SortedListIteratorPtr iter3 = SLCreateIterator(list);
        
        SLNextItem(iter3);
        SLNextItem(iter3);
        SLRemove(list, strs[4]);
        
        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            
            printf("output: %lu\n", strlen(((char*)curr->Object)));
        }
        
        SLNextItem(iter);
        SLDestroyIterator(iter);
        SLDestroyIterator(iter3);
        SLDestroyIterator(iter2);
        SLDestroy(list);
        
    }
    else if (c == '6')
    {
        SortedListPtr list = SLCreate(compareStrings, destroy);
        
        for(i = 0; i < 7; i++)
            SLInsert(list, strs[i]);
        
        SortedListIteratorPtr iter = SLCreateIterator(list);
        
        for(i=0;i<7;i++)
            SLRemove(list, strs[i]);
        
        for (i = 0; i < 100; i++) //try to break iterator
            SLNextItem(iter);
        
        SLRemove(list, strs[0]); //try to remove object that isn't in the list
        
        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            printf("output strlen: %lu\n", strlen(((char*)curr->Object)));
        }
        
        SLDestroyIterator(iter);
        SLDestroy(list);
    }
    else if (c =='7')
    {
        /*
         * This case tests a new object type.
         * This is a simple load, no deletes or iterators are used.
         */
        
        SortedListPtr list = SLCreate(compareInts, destroy);
        
        for (i=0; i< 5; i++) {
            int *ptr;
            ptr = &numbers[i];
            SLInsert(list, ptr);
        }

        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            printf("output int: %d\n", *((int *)curr->Object));
        }
        
        SLDestroy(list);
    }
    else if (c == '8')
    {
        /*
         * Load the LL with int object, then proceed to
         * created an iterator. Nuke the current target of the iterator
         * which is also the head. Increment iterator and check valid LL
         */
        
        SortedListPtr list = SLCreate(compareInts, destroy);
        int *ptr;
        
        for (i=0; i< 5; i++) {

            ptr = &numbers[i];
            SLInsert(list, ptr);
        }
        
        SortedListIteratorPtr iter = SLCreateIterator(list);
        int x = 7;
        ptr = &x; //remove head while iterator is targeting
       // SLRemove(list, ptr);
        SLNextItem(iter); //advance iterator after making target ghost node
        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            
            printf("output int: %d\n", *((int *)curr->Object));
        }
        
        SLDestroy(list);
        SLDestroyIterator(iter);
    }
    else if(c == '9')
    {
        /*
         * This case is for testing structs for storing in the LL.
         * Load structs into LL and test for correct order.
         */
        
        SortedListPtr list = SLCreate(compareStrings, destroy);
        Struct_Obj *ptr;
        
        for (i=0; i< 7; i++) {
            
            ptr = &strtarr[i];
            SLInsert(list, ptr);
        }
        

        Node curr = NULL;
        
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            printf("output int: %d\n", *((int *)curr->Object));
        }
        
        SLDestroy(list);
    }
    else if(c == '0')
    {
        SortedListPtr list = SLCreate(compareStrings, destroy);
        Struct_Obj *ptr;
        
        for (i=0; i< 7; i++) {
            
            ptr = &strtarr[i];
            SLInsert(list, ptr);
        }
        
        SortedListIteratorPtr iter = SLCreateIterator(list);
        ptr = &strtarr[5];
        
        SLRemove(list, ptr); //remove head / iterator target
        
        for(i=0;i<20;i++) //iterate past end of LL
            SLNextItem(iter);
        
        
        Node curr = NULL;
    
        for(curr = list->next; curr != NULL; curr = curr->next)
        {
            if(!curr->Object)
                continue;
            printf("output int: %d\n", *((int *)curr->Object));
        }
        
        SLDestroy(list);
        SLDestroyIterator(iter);
    }
    else
    {
        printf("bad input, please run the program again, proper input is 0-9\n");
    }
    return 0;
    
}

int comparator(void *o1, void *o2)
{
    //using strings for ease of testing
    return (int)strlen(*((char**)o1)) - (int)strlen(*((char**)o2));
}

void destructor(void *o){
    //free(o);
}


