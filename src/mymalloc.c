#include "malloc.h"

void check(int depth);


int main()
{
    printf("***Test Cases***\n\n");
    printf("Ref: sizeof(Heap): %d\n", sizeof(Heap));
    printf("Ref: sizeof(Block): %d\n\n", sizeof(Block));
	/* 
		TEST CASE 1: bug bashing, unfixed bucket.
        Call to the function, no seg-faults,
        mutable pointer is returned. Access,
        set, and print data from sequential
        addresses. Bucket created should be unfixed
        and head of unfixed DLL.
	 */
    printf("***Test Case 1: unfixed bukcet malloc, size = 1100***\n\n");
	char * tc1 = (char *) mymalloc(sizeof(char) * 1100);
	
	int i;

	for( i = 0; i <= 26; i++)
	{
        tc1[i] = (char) i + 97;
	}

	tc1[26] = '\0';
    printf("\nTest Case 1 RESULT: %s\n", tc1);
    printf("Memory address of tc1 is: %p\n\n", (void *)tc1);
    
    /*
        TEST CASE 2: bug bashing, fixed bucket. 
        Call functions, no-seg faults,
        mutable pointer is returned.Access,
        set, and print data from sequential
        addresses. Bucket created should be fixed
        and head of fixed DLL.
     */
    printf("***Test Case 2: fixed bukcet malloc, size = 256***\n\n");
    char * tc2 = (char *) mymalloc(sizeof(char) * 256);
    for( i = 0; i <= 26; i++)
    {
        tc2[i] = (char) i + 97;
    }
    
    tc2[26] = '\0';
    printf("\nTest Case 2: %s\n", tc2);
    printf("Memory address of tc2 is: %p\n\n", (void *)tc2);

    /*
        TEST CASE 3: bug bashing, check alignment.
        User [] operator to write into tc2's space, 
        from tc1.
        &tc2 - &tc1 = 8000
     */
    printf("***Test Case 3: Alignment check, corrupt tc2 via tc1[]***\n\n");
    for(i = (int)(tc2 - tc1) ; i < ((int)(tc2 - tc1)) + 10; i++)
    {
        tc1[i] = '$';
        printf("Corrupting memory address: %p\n", (void *)&tc1[i]);
    }
    
    printf("\nTest Case 3: %s\n", tc2); // should be corrupted;
    printf("Memory distance between tc2 and tc1: %d\n\n" , (int)(tc2 - tc1));
	return 0;

}


void check(int depth) {
    char c;
    char *ptr = malloc(1);
    printf("stack at %p, heap at %p\n", &c, ptr);
    if (depth <= 0) return;
    check(depth-1);
}
