#include "malloc.h"
#include <unistd.h>

/* this version uses sbrk */

/*
	The goal behind this implementation of malloc is to minimize the issue of defragmentation
	by having two linked DLLs. A DLL of fixed size buckets will grow from one end, while another
	containing non-fixed values will grow from the other end. 

	ROOT																  LAST
	   \===================================================================|
	 UNFIXED -->                                                      <-- FIXED

	UNFIXED:
	   |Block|===allocated heap===| |NEXT GETS APPENDED at + Size + sizeof(Block)|
	 		 ^
	 		 |
	 		pointer *

	FIXED:
	|NEXT GETS APPENDED AT (fixed_tail - (size + sizeof(Block)))| |===allocated heap===|Block|
	 													  		  ^
	 															  |
	 													        pointer *
	
	Bounds checking of either sub DLL will need to be done everytime a new entry is added on either side.
	Neither sub DLL will have a implicit reference to eachother from their *prev or *succ pointers. 
 */
void *
mymalloc( size_t size )
{
	/*
		root = aggregate heap block head, head of of unfixed DLL
		last = aggregate heap tail, head of fixed DLL

		unfixed_tail = pointer to end of unfixed bucket_size DLL
		fixed_tail = pointer to end of fixed bucket_size DLL
		TODO: allocated new block of heap, these variables can't switch to and from
		said blocks as nicely, most likely will need to perform O(n) search to find tail:

		REGION:
				root + 0 |=======|                       |==============| last + 0

				root + 1 |================|      |======================| last + 1  

				root + 2 |====|                            |============| last + 2

				...

				root + k |===========================|    |=============| last + k

	 */
	static ** Heap heap; // TODO: add dynamical scaling w/o using GNU malloc 
	static int i = 0;
	int bucket_flag;

	// first time initialization, might need error checking
	if ( !heap )
	{
		heap[i] = (Heap *) sbrk(8192);
		heap[i]->root = (Block *) ( (char *) heap + sizeof(Heap)); // 2^13
		heap[i]->last = sbrk(0);
		heap[i]->unfixed_tail = NULL; // MIGHT NEED TO CHANGE THESE INITIALIZATIONS
		heap[i]->fixed_tail = NULL;  // MIGHT NEED TO CHANGE THESE INITIALIZATIONS 
		i++;
	}

	// Iterator variables 
	Block memEntry *p, *next;

	int k;
	int j = -1;

	for(k = 0; k < i; k++)
	{
		if(heap[k].in_use < size){
			j = k;
			break;
		}
	}
	
	if( j < 0){

		j = i;
		heap[i] = (Heap *) sbrk(8192);
		heap[i]->root = (Block *) ( (char *) heap + sizeof(Heap)); // 2^13
		heap[i]->last = sbrk(0);
		heap[i]->unfixed_tail = NULL; // MIGHT NEED TO CHANGE THESE INITIALIZATIONS
		heap[i]->fixed_tail = NULL;  // MIGHT NEED TO CHANGE THESE INITIALIZATIONS 
		i++;

	}

	ALLOCATE:
	// Check if it goes in a pre-defined bucket
	if ( size <= BUCKET_SIZE * 6)
	{
		p = heap[j]->last;
		size = fit_bucket( size );
		bucket_flag = 1;
	}
	else
	{
		p = heap[j]->root;
		bucket_flag = 0;
	}
	
	// Traverse List, this tries to use free'd bucket 
	for(; p != 0, p = p->next)
	{
		if ( p->size < size || !p->isfree)
			continue;

		if ( p->size <= size && p->isfree ) 
		{ 
			p->isfree = 0;

			if(bucket)
				return (char *)p - p->size;	
			else
				return (char *)p + sizeof(Block);
		}else
			continue;
	}

	// Bounds checking 
	if ( (bucket_flag ? ((heap[j]->fixd_tail - size - sizeof(block)) >= heap[j]->unfixed_tail) : (heap[j]->unfixed_tail + size + sizeof(block)) <= head[j]->fixd_tail) )
	{
		if ( bucket_flag )
		{
			// first element
			if ( heap[j]->fixed_tail == NULL )
			{
				p = heap[j]->last;
				p -= (Block *) ( (char *) - sizeof(Block) - 1);
				heap[j]->fixed_tail = p;
				heap[j]->last = p;
				p->next = NULL;
				p->prev = NULL; // could make this some sort of indicator to the next heap level (if it exists)
				p->size = size;
				p->isfree = 0;
				return (char *)p - size;	

			}
			else
			{
				p = heap[j]->fixed_tail;
				p -= (Block *) ( (char *) sizeof(Block) + heap[j]->fixed_tail->size - 1); //move it to the next avail location;
				p->prev = heap[j]->fixed_tail;
				heap[j]->fixed_tail->next = p;
				p->size = size;
				p->isfree = 0;
				heap[j]->unfixed_tail = p;
				heap[j]->unfixed_tail->next = NULL;
				return (char *)p - size;
				/*
					FIXED:
					|NEXT GETS APPENDED AT (fixed_tail - (size + sizeof(Block)))| |===allocated heap===|Block|
											  		  							  ^
	 																			  |
	 													      			return	pointer *
				 */	

			}

		}
		else
		{
			// first element 
			if ( heap[j]->unfixed_tail == NULL)
			{	
				p = heap->root;
				heap[j]->unfixed_tail = p;
				heap[j]->root = p;
				p->next = NULL;
				p->prev = NULL; // could make this some sort of indicator to the next heap level (if it exists)
				p->size = size;
				p->isfree = 0;
				return (char *)p + sizeof(Block);	// could just return p + 1 
			}
			else
			{
				p = (Block *) ( (char *) sizeof(Block) + unfixed_tail->size + 1); //move it to the next avail location;
				p->prev = heap[j]->unfixed_tail;
				heap[j]->unfixed_tail->next = p;
				p->size = size;
				p->isfree = 0;
				heap[j]->unfixed_tail = p;
				heap[j]->unfixed_tail->next = NULL;
				return (char *)p + sizeof(Block);
			}
		}
	}
	else
	{
		j = i;
		heap[i] = (Heap *) sbrk(8192);
		heap[i]->root = (Block *) ( (char *) heap + sizeof(Heap)); // 2^13
		heap[i]->last = sbrk(0);
		heap[i]->unfixed_tail = NULL; // MIGHT NEED TO CHANGE THESE INITIALIZATIONS
		heap[i]->fixed_tail = NULL;  // MIGHT NEED TO CHANGE THESE INITIALIZATIONS 
		i++;
		goto ALLOCATE;
	}

}

void
myfree( void * p )
{
	Block memEntry *ptr, *pred, *next;
	ptr = (Block  *)( (char *)p - sizeof( Block) ); // factor out overhead 

	if ( ( pred = ptr->prev ) != 0 && pred->isfree )
	{
		pred->size += sizeof( Block) + ptr->size;
		pred->next = ptr->next;
		if ( ptr->next != 0 ) ptr->next->prev = pred;
	}
	else
	{
		ptr->isfree = 1;
		pred = ptr;
	}
	
	if ( ( next = ptr->next ) != 0 && next->isfree )
	{
		pred->size += sizeof(Block ) + next->size;
		pred->next = next->next;
		if ( next->next != 0 ) next->next->prev = pred;
	}
}

int
fit_bucket( unsigned int size )
{
	int i;
	for(i = 1; i <= 6; i++)
	{
		if( size <= (BUCKET_SIZE * i) )
		{
			return BUCKET_SIZE * i;
		}
	}

	return 0;
}


