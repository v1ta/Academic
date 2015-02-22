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

void SLDestroy(SortedListPtr list)
{
    //check to make sure a valid list was passed
    if (!list)
    {
        return;
    }
    else if (!list->next) //if valid check if it contains at least 1 node
    {
        free(list);
        return;
    }
    
    //set up temp variables for LL traversal
    Node curr = list->next;
    Node prev = curr;
    
    //free nodes in O(n) time
    while(curr->next)
    {
        curr = curr->next;
        free(prev);
        prev = curr;
    }

    free(curr);
    free(list); //free the head
    
    return;
}




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
    
    //Traverse list in O(n) (worst case),
    for (curr = list->next; curr!=NULL; curr = curr->next)
    {
        curr->ref_count++; //let the program know this node is being referenced
        
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


int SLRemove(SortedListPtr list, void *newObj)
{
    //check for valid input;
    if(!list || !newObj)
        return 0;
    else if(list->nodes < 1) //make sure list has contents before executing search
        return 0;
    
    //make a copy of the address
    void *data = newObj;
    
    //prepare LL traversal
    Node curr = list->next;
    Node prev = NULL;
    
    
    //Traverse in O(n) worst case
    while(curr)
    {
        //let the program know this node is being referenced
        curr->ref_count++;
        
        //check if node contains object, destroy if it does.
        if (curr->Object == data)
        {
            //this object is in use by another part of the program
            if (curr->ref_count > 1) {
                if(prev)
                    prev->ref_count--;
                curr->ref_count--;
                return 0;
            }
            
            list->destruct(curr->Object);
            list->nodes--;
            
            //relink list
            if (!prev)
                list->next = curr->next;
            else{
                prev->next = curr->next;
                prev->ref_count--;
            }
            
            free(curr);
            
            return 1;
        }
        
        if(prev)
            prev->ref_count--; //tell the program this node has been left
        
        prev = curr;
        curr = curr->next;
    }
    
    return 0;
}


SortedListIteratorPtr SLCreateIterator(SortedListPtr list)
{
    //check for a valid list, then check if the list has at least one node.
    if(!list)
        return NULL;
    else if (list->nodes < 1)
        return NULL;
    
    SortedListIteratorPtr iter = (SortedListIteratorPtr)malloc(sizeof(struct SortedListIterator));
    
    //set iterator to front of list
    iter->curr_node = list->next;
    iter->curr_node->ref_count++;
    
    return iter;
}


void SLDestroyIterator(SortedListIteratorPtr iter)
{
    //check for valid input
    if(!iter)
        return;
    
    //not sure if this is necessary, but it makes sure the iterator isn't pointing towards anything.
    iter->curr_node->ref_count--;
    iter->curr_node = NULL;
    
    //check if @ beginning of list
    if(iter->prev_node)
    {
        iter->prev_node->ref_count--;
        iter->prev_node = NULL;
    }
    
    free(iter);
    
    return;
}



void * SLGetItem(SortedListIteratorPtr iter )
{
    //check for valid input
    if (!iter)
        return 0;
    
    return iter->curr_node->Object;
}



void * SLNextItem(SortedListIteratorPtr iter)
{
    //check for valid iterator
    if(!iter)
        return NULL;
    
    //check for end of list
    if(!iter->curr_node->next)
    {
        printf("test\n");
        return NULL;
    }
    
    //check to see if at beginning of list
    if (iter->prev_node)
        iter->prev_node->ref_count--;
    
    //increment
    iter->prev_node = iter->curr_node;
    iter->curr_node = iter->curr_node->next;
    iter->curr_node->ref_count++;
    
    //return object
    return iter->curr_node->Object;
}







