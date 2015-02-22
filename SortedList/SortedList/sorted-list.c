//
//  sorted-list.c
//  SortedList
//
//  Created by Joseph DeVita on 2/21/15.
//  Copyright (c) 2015 Joseph DeVita. All rights reserved.
//

#include "sorted-list.h"
#include "stdlib.h"
#include "stdio.h"

SortedListPtr SLCreate(CompareFuncT cf, DestructFuncT df)
{
    
    SortedListPtr list_ptr = (SortedListPtr) malloc(sizeof(struct SortedList));
    
    //check to make sure pointer was properly allocated memory.
    if (!list_ptr)
    {
        return NULL;
    }
    
    //initialize head of LL
    list_ptr->nodes = 0;
    list_ptr->next = NULL;
    list_ptr->comparator = cf;
    list_ptr->destruct = df;
    
    return list_ptr;
}

Node SLNCreate(void *newObj)
{
    Node new_node = (Node) malloc(sizeof(struct SortedListNode));
    
    //check to make sure memeory was properly allocated
    if (!new_node) {
        return NULL;
    }
    
    //initialize default values
    new_node->Object = newObj;
    new_node->ref_count = 0;
    new_node->next = NULL;
    
    return new_node; //return new node with default values;
}

void SLDestroy(SortedListPtr list);


int SLInsert(SortedListPtr list, void *newObj)
{
    if (!list || !newObj) //check for non-NULL pointers
    {
        return 0; //return failure value;
    }
    
    
    if (list->nodes == 0) // empty list insert object;
    {
        list->next = SLNCreate(newObj);
        list->nodes++;
        return 1;
    }
    
    Node curr = NULL; // LL iterator
    Node temp = (Node)malloc(sizeof(struct SortedListNode));
    Node prev = NULL; // Previous entry
    
    for (curr = list->next; curr!=NULL; curr = curr->next)
    {
        curr->ref_count++; //increment reference count while using object;
        
        if (list->comparator(curr->Object, newObj) > 0)
        {
            if (curr->next == NULL)
            {
                curr->next = SLNCreate(newObj);
                curr->ref_count--;
                list->nodes++;
                
                if (prev)
                    prev->ref_count--;
                
                break;
            }

            if (prev)
                prev->ref_count--;
            
            prev = curr;
            
            
            continue; //new object is smaller, keep traversing LL
        }
        else if (list->comparator(curr->Object, newObj) == 0)
        {
             printf("test\n");
    
            //new Objec is equal in size, so it is safe to insert it @ next;
            temp = curr->next;
            curr->next = SLNCreate(newObj);
            curr->next->next = temp;
            curr->ref_count--;
            list->nodes++;
            
            if (prev)
                prev->ref_count--;
            
            break;
        }
        else if (list->comparator(curr->Object, newObj) < 0)
        {
            if(!prev) // at front of list
            {
                //new object is larger, so insert @ list head
                list->next = SLNCreate(newObj);
                list->next->next = curr;

  
                curr->ref_count--;
                list->nodes++;
                
                if (prev)
                    prev->ref_count--;
                
                break;
            }
            else
            {
                //new object is larger, so insert @ previous node
                prev->next = SLNCreate(newObj);
                prev->next->next = curr;
                curr->ref_count--;
                list->nodes++;
                
                if (prev)
                    prev->ref_count--;
                
                break;
            }
        }
        if(prev)
            prev->ref_count--; //no longer visiting node, decrement reference count
        
        prev = curr; //previous will always be one behind current
    }
    
    return 1; //sucessful insert
}


int SLRemove(SortedListPtr list, void *newObj);


SortedListIteratorPtr SLCreateIterator(SortedListPtr list);


void SLDestroyIterator(SortedListIteratorPtr iter);



void * SLGetItem( SortedListIteratorPtr iter );



void * SLNextItem(SortedListIteratorPtr iter);
