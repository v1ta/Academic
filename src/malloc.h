/* 
	The smallest bucket >= sizeof(memEntry). This will vary depending on
	your system. For example a 32 bit system can have a minmum bucket size of
	16, where 64 bit systems will only allow for 32.

						32-bit			64-bit
	BUCKET_SIZE		1	16				32
					2	32				64
					3	64				128
					4	128				256
					5	256				512
					6	512				1024

	in KnR they use long as opposed to *. This is also for power of 2 alignment as opposed to "pure" alignment. 
 */
#define BUCKET_SIZE ( sizeof(int *) * 4) 

extern unsigned int * storage;
/*
	mmap() can be used to store values that are > our max page file. 
 */
#define MMAP(addr, size, prot, flags) \
 __mmap((addr), (size), (prot), (flags)|MAP_ANONYMOUS|MAP_PRIVATE, -1, 0);

/*
	DLL implementation:
	*prev = pointer to previous entry
	*succ = pointer to next entry
	isfree = 0 for false(no) 1 for true(yes)
	size = amount of memory past struct that is allocated for the pointer
 */

typedef struct block
{
	Block memEntry *prev, *next;
	int isfree; //boolean off / on?
	size_t size;
} Block;

/*
	Allows a convient way of accessing newly allocated heaps. 
 */
typedef struct heap
{
	unsigned int in_use;  // allows a way to know if we should check a heap after its full, note: this doesn't gurantee it can be used. 
	Block *root, *last;
	Block *unfixed_tail, *fixed_tail;
	
} Heap;


/*
	Standard malloc, will allocated an amount of heap memory and return a void *
	to the newly allocated space, an argument of 0 is passed, or there isn't enough
	space available in the system a null pointer will be returned. 
 */
void * mymalloc( size_t );

/*
	Accepts an unsized int and returns a fixed bucket size or 0 on failure
 */
int fit_bucket( size_t );

void decrement_heap( void * );

void myfree( void * );
