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
    /*
    else if (!list->next->Object) //first node is a ghost node
    {
        list->next->Object = newObj;
        return 1;
    }
    */
    Node curr = NULL; // LL iterator
    Node temp = NULL;//(Node)malloc(sizeof(struct SortedListNode));
    Node prev = NULL; // Previous entry
    
    //Traverse list in O(n) (worst case),
    for (curr = list->next; curr!=NULL; curr = curr->next)
    {
        
    GARBAGE_COLLECTION:
        /*
         * Due to the requirement that we use a singly linked list,
         * running into a ghost node presents us with a few natrual options.
         * If we opt to re-use the node as opposed to trashing it, we'd have to
         * do a signficant amount of jumps not only to compare, but to relink. 
         * Given that all of the jumps would require extra functions, or extra
         * variables, I've opted to simply delete the node as I've dedued that 
         * it would be more efficent for the insert method as a whole. Also
         * this isn't the only function capable of garbage collection. It is being 
         * add here for the efficency gain do to LL searh being O(n) time, it makes
         * sense to shorten said (n) if given the oppurtunity as opposed to 
         * favoring encapsulation (imo). 
         */

        
        if (curr)
        {
            
            if (!curr->Object)
            {
            //make sure the node isn't being referenced
                if(curr->ref_count == 0)
                {
                    //check to see if its the first node in the list
                    if (!prev){
                        list->next = curr->next;
                    }else{
                        prev->next = curr->next;
                    }
                    temp = curr;
                    curr = curr->next;
                    free(temp);
                    list->nodes--;
                
             
                    goto GARBAGE_COLLECTION;
                
                }
                else //incase the ghost object has an iterator
                {
                    prev = curr;
                    curr = curr->next;
                    goto GARBAGE_COLLECTION;
                }
            
            }
        }
        else //ran into end of list
        {
            curr = SLNCreate(newObj);
            list->nodes++;
            prev->next = curr;
            break;
        }

        
        if (list->comparator(curr->Object, newObj) > 0)
        {
            if (curr->next == NULL)
            {
                curr->next = SLNCreate(newObj);
                list->nodes++;
                
                break;
            }
            
            prev = curr;
            
            
            continue; //new object is smaller, keep traversing LL
        }
        else if (list->comparator(curr->Object, newObj) == 0)
        {
    
            //new Objec is equal in size, so it is safe to insert it @ next;
            temp = curr->next;
            curr->next = SLNCreate(newObj);
            curr->next->next = temp;
            list->nodes++;
            
            break;
        }
        else if (list->comparator(curr->Object, newObj) < 0)
        {
            if(!prev) // at front of list
            {
                //new object is larger, so insert @ list head
                list->next = SLNCreate(newObj);
                list->next->next = curr;

  
                list->nodes++;
                
                break;
            }
            else
            {
                //new object is larger, so insert @ previous node
                prev->next = SLNCreate(newObj);
                prev->next->next = curr;
                list->nodes++;
                

                
                break;
            }
        }
        
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
        if (curr->Object) //ghost node
        {
            
        //check if node contains object, destroy if it does.
        if (list->comparator(curr->Object,data) == 0)
        {
            //this object is in use by another part of the program
            if (curr->ref_count > 0)
            {
                //curr->Object = NULL;
                list->destruct(curr->Object);
                if(curr->Object)
                    curr->Object = NULL;
                return 1; //object freed, but node becomes ghost
            }
            
            list->destruct(curr->Object);
            list->nodes--;
            
            //relink list
            if (!prev)
                list->next = curr->next;
            else{
                prev->next = curr->next;
            }
            
            free(curr);
            
            return 1;
        }
        }
        
        prev = curr;
        curr = curr->next;
    }
    
    return 0;
}


SortedListIteratorPtr SLCreateIterator(SortedListPtr list)
{
    //check for a valid list, then check if the list has at least one node.
    if(!list)
    {
        return NULL;
        
    }
    else if (list->nodes < 1)
    {
        return NULL;
    }
    
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
    if (iter->curr_node) {
        iter->curr_node->ref_count--;
    }

    
    //check if @ beginning of list
    if(iter->prev_node)
    {
        iter->prev_node->ref_count--;
    }
    
    free(iter);
    
    return;
}



void * SLGetItem(SortedListIteratorPtr iter )
{
    //check for valid input
    if (!iter)
        return 0;
    
    //check if iterator advanced passed list.
    if(!iter->curr_node)
        return 0;
    
    //Make sure an object exists. 
    if (iter->curr_node->Object) {
        return iter->curr_node->Object;
    }
    
    return 0;
    
}



void * SLNextItem(SortedListIteratorPtr iter)
{

    if(!iter)
    {
        return NULL;
    }
    else if (!iter->curr_node) // at end of list
        return NULL;
    
    //check for end of list
    if(!iter->curr_node->next)
    {
        iter->curr_node = NULL;
        return NULL;
    }
    

    
    //increment
    
    do{
        //This checks to make sure the next node isn't end of list
        if (!iter->curr_node->next)
            return NULL;
        
        //check to see if at beginning of list
        if (iter->prev_node)
            iter->prev_node->ref_count--;
        
        iter->prev_node = iter->curr_node;
        iter->curr_node = iter->curr_node->next;
        iter->curr_node->ref_count++;
    }while(!iter->curr_node->Object);
    
    /*
     * loop until valid node found, it will essentially check if the current
     * node is a ghost node, if it is, it will jump to the next.
     */
    
    //return object
    return iter->curr_node->Object;
}







