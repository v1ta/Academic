
#include "bank_server.h"

// To print clients that disconnect

//#define MAP_ANONYMOUS MAP_ANON //cause cool kids use OSX
#define ERROR -1


//42968
//49926

static inline
int sem_wait_nointr(sem_t *sem) {
  while (sem_wait(sem))
    if (errno == EINTR) errno = 0;
    else return -1;
  return 0;
}

void client_session ( void * arg, Repo * repo, Bank * bank)
{
	int			sd;
	char			request[2048];
	//char			response[2048];
	char  data[2048];
	//char			temp;
	//int			i;
	//int			limit, size;
	//float			ignore;
	//long			senderIPaddr;
	//int 		session = 0;
	Repo * client_repo = repo;
	Bank * client_bank = bank;
	


	sd = *(int *)arg;
	free( arg );					// keeping to memory management covenant

	/*

		pthread_detach allows for a thread to "clean up" after itself.
		a pthread_join() is not necessary to clean up the resources.

	 */

	//pthread_detach( pthread_self() );		// Don't join on this 

	/*
	
		read(1 ,2 ,3)

		1 = file descriptor/stream (i.e. 1 for stdin, 2 for stderr, <var>)
		2 = buffer (void *), store stuff from 1
		3 = the amount you want to read from 1

		return is negative if error and 0 if end-of-file

	 */


	printf("SERVER: Created client service process %d\n", getpid());

	printf("SERVER: A client has connected to the server\n");

	while ( read( sd, request, sizeof(request) ) > 0 )
	{	
		char command = request[strlen(request) - 1];
		request[strlen(request) - 1] = '\0'; //delete flag
		printf( "SERVER: receives input:  %s\n", request );

    	

    	switch(command)
    	{
    		case '1':
    			printf("SERVER: ACCOUNT CREATION REQESTED FROM PID %d \n", getpid());
    			sprintf(request, create_account(client_repo,client_bank,request), sizeof(request));
    			//printf("SERVER: ACCOUNT CREATION FROM PID %d: %s\n", getpid(), request);
    			write( sd, request, strlen(request) + 1 );
    			break;
			case '2':
				printf("SERVER: ACCOUNT SERVE REQUESTED FROM PID %d \n", getpid());
				strncpy(data, request, sizeof(request));
				serve_account(client_bank, data, sd);
				//write( sd, request, strlen(request) + 1 );
				break;
			default:
				printf("SERVER: RECEIEVED INCORRECT INPUT\n");
				strncpy(request, "SERVER: RECEIEVED INCORRECT INPUT\n", 34);
				//sprintf(request, "SERVER: RECEIEVED INCORRECT INPUT\n", strlen(request));
				write( sd, request, strlen(request) + 1 );

    	}

    	    	/*
		size = strlen( request );
		limit = strlen( request ) / 2;


		for ( i = 0 ; i < limit ; i++ )
		{
			temp = request[i];
			request[i] = request[size - i - 1];
			request[size - i - 1] = temp;
		}
		*/

		
	}
	close( sd );
	

}


void serve_account(Bank * accounts, char * account_name, int sd)
{	

	char socket_buffer[512];
	char request[512];
	int account_index = - 1;
	int i = 0;
	int count = 0;
	double to_depo = 0;
	double to_with = 0;
	int temp = 0;
		
	//make sure account exists 
	for(i = 0; i < 20; i++)
	{
		if( stringCompare(accounts[i].account, account_name) == 1 )
		{
			account_index = i;
			break;
		}
	}

	//memset(&socket_buffer[0], 0, sizeof(socket_buffer));
	if(account_index == -1){

		sprintf(socket_buffer, "Account: %s doesn't exist", account_name);
		write( sd, socket_buffer, strlen(socket_buffer) + 1 );

	}

	//sem_wait(sem[account_index]);
	
	while( count < 5 ){

		if(sem_trywait(sem[account_index]) == 0 ){
			memset(&socket_buffer[0], 0, sizeof(socket_buffer));
			sprintf(socket_buffer, "Accessing account: %s", account_name);
			socket_buffer[strlen(socket_buffer)] = '~';
			socket_buffer[strlen(socket_buffer)] = '\0';
			break;
		}
		sprintf(socket_buffer, "Account: %s is in session", account_name);
		socket_buffer[strlen(socket_buffer)] = '&';
		socket_buffer[strlen(socket_buffer)] = '\0';
		write( sd, socket_buffer, strlen(socket_buffer) + 1 );
		memset(&socket_buffer[0], 0, sizeof(socket_buffer));
		count++;
		sleep(2);
	}
		if(count == 5){
			sprintf(socket_buffer, "Timed out...");
			write( sd, socket_buffer, strlen(socket_buffer) + 1);
			return;
		}
	



		//put the client in session mode for account[account_index]
		write( sd, socket_buffer, strlen(socket_buffer) + 1 );
		sprintf(request, socket_buffer, strlen(socket_buffer));
		accounts[account_index].in_session = 1;
		INPUT:
		while ( read( sd, socket_buffer, sizeof(socket_buffer) ) > 0 ) {
			char command = socket_buffer[strlen(socket_buffer) - 1];
			socket_buffer[strlen(socket_buffer) - 1] = '\0';
			switch(command)
			{
				case '1':
					printf("SERVER: DEPOSIT REQUEST FOR ACCOUNT: %s\n", accounts[account_index].account);
					to_depo = atof(socket_buffer);
					
					if(to_depo <= 0){
						to_depo = 0.0;
						sprintf(socket_buffer, "Deposits must be greater than 0");
						write(sd, socket_buffer, strlen(socket_buffer) + 1);
						memset(&socket_buffer[0], 0, sizeof(socket_buffer));
						break;
					}else{
						accounts[account_index].balance += to_depo;
						sprintf(socket_buffer, "Deposisted: %.2f, your current balance is: %.2f", to_depo, accounts[account_index].balance);
						write(sd, socket_buffer, strlen(socket_buffer) + 1);
						memset(&socket_buffer[0], 0, sizeof(socket_buffer));
					}
					break;
				case '2':
					printf("SERVER: WITHDRAWL REQUEST FOR ACCOUNT: %s\n", accounts[account_index].account);
					to_with = atof(socket_buffer);
					
					if(accounts[account_index].balance < to_with && to_with > 0)
					{
						sprintf(socket_buffer, "Account balance: %.2f, is not high enough to withdraw: %.2f", accounts[account_index].balance, to_with);
						write(sd, socket_buffer, strlen(socket_buffer) + 1);
						printf("SERVER: WITHDRAWL REQUEST FOR ACCOUNT: %s DENIED\n", accounts[account_index].account);
						memset(&socket_buffer[0], 0, sizeof(socket_buffer));
					}else if( to_with <= 0){
						sprintf(socket_buffer, "Amount: %.2f invalid.... Please withdraw an amount > 0",to_with);
						write(sd, socket_buffer, strlen(socket_buffer) + 1);
						printf("SERVER: WITHDRAWL REQUEST FOR ACCOUNT: %s DENIED\n", accounts[account_index].account);
						memset(&socket_buffer[0], 0, sizeof(socket_buffer));

					}else{
						accounts[account_index].balance -= to_with;
						sprintf(socket_buffer, "Withdrew: %.2f, your current balance is: %.2f", to_with, accounts[account_index].balance);
						write(sd, socket_buffer, strlen(socket_buffer) + 1);
						printf("SERVER: WITHDREW %.2f FROM ACCOUNT: %s SUCESSFUL\n", to_with ,accounts[account_index].account);
						memset(&socket_buffer[0], 0, sizeof(socket_buffer));
					}
					break;
				case '3':
						printf("SERVER: QUERY FOR ACCOUNT: %s\n" ,accounts[account_index].account);
						sprintf(socket_buffer, "Current balance: %.2f", accounts[account_index].balance);
						write(sd, socket_buffer, strlen(socket_buffer) + 1);
						memset(&socket_buffer[0], 0, sizeof(socket_buffer));
						break;
				case '4':
						printf("SERVER: ENDING SESSION FOR ACCOUNT: %s\n" ,accounts[account_index].account);
						accounts[account_index].in_session = 0;
						sprintf(socket_buffer, "Ending Session for account: %s", accounts[account_index].account);
						socket_buffer[strlen(socket_buffer)] = '~';
						socket_buffer[strlen(socket_buffer)] = '\0';
						write(sd, socket_buffer, strlen(socket_buffer) + 1);
						memset(&socket_buffer[0], 0, sizeof(socket_buffer));
						sem_post(sem[account_index]);
						return;
					
				default:
					sprintf(socket_buffer, "Invalid input");
					write(sd, socket_buffer, strlen(socket_buffer) + 1);
					memset(&socket_buffer[0], 0, sizeof(socket_buffer));
					goto INPUT;
			}

		}

	sem_post(sem[account_index]);

	return;
}


int stringCompare(char str1[],char str2[]){
    int i=0,flag=0;
   
    while(str1[i]!='\0' && str2[i]!='\0'){
         if(str1[i]!=str2[i]){
             flag=1;
             break;
         }
         i++;
    }

    if (flag==0 && str1[i]=='\0' && str2[i]=='\0')
         return 1;
    else
         return 0;

}


void print_bank(Repo * repo, Bank * accounts){

	char SUPERBUFFER[2000];	

	//create seperate pointers
	Repo * repoT = repo;
	Bank * accountsT = accounts;

	while(server){
		
			sleep(20);
			sem_wait_nointr(rep);
			sprintf(SUPERBUFFER, "\n******BANK******\n");
			for(int i = 0; i < 20; i++){

				if(!repoT[i].avail){

					if(accounts[i].in_session){

						//if it can post it isn't actually in session
						if (sem_trywait(sem[i]) == 0 )
						{
							accountsT[i].in_session = 0;
							sem_post(sem[i]);
							goto NOSESSION;
						}

						strncat(SUPERBUFFER, "ACCOUNT: ", 9);
						strncat(SUPERBUFFER, accountsT[i].account, sizeof(accountsT[i].account) + 1);
						strncat(SUPERBUFFER, ", IN-SESSION\n", 13);
					}else{
						NOSESSION: 
						strncat(SUPERBUFFER, "ACCOUNT: ", 9);
						strncat(SUPERBUFFER, accountsT[i].account, sizeof(accountsT[i].account) +1);
						strncat(SUPERBUFFER, "\n", 1);
					}
				}else{

					break;

				}

			}
			
			printf("%s\n", SUPERBUFFER);
			sem_post(rep);
			memset(&SUPERBUFFER[0], 0, sizeof(SUPERBUFFER));
	}
	

}



char * create_account(Repo * repo, Bank * accounts, char * name){

	/*
	 * The bank itself doesn't have to be locked, given the structure of the program,
	 * Repo allows for two layers of synchronization.
	 */

	//lock the repository
	sem_wait(rep);

	int i;
	int insert = -1;

	//make sure account name is in available
	for(i = 0; i < 20; i++)
	{
		if( stringCompare(repo[i].account, name) == 1 )
		{	sem_post(rep);
			return "FAILED: That account-name is in use";
		}
	}


    //make sure the bank has room
	for(i = 0; i < 20; i++){

		if(repo[i].avail ){

			insert = i;
			repo[i].avail = 0;
			break;
		}
	}

	if(insert == -1){
		sem_post(rep);
		return "FAILED: Bank is at maximum capacity";
	}

	//add new account at next available spot
	strncpy(repo[insert].account, name, strlen(name));
    strncpy(accounts[insert].account, repo[insert].account, strlen(repo[insert].account));


    //unlock the repository
    sem_post(rep);
	return "SUCCESS: Account succesuflly created";
}




// Miniature server to exercise getaddrinfo(2).



Bank * createBank ( size_t size )
{

	//neccessary arguments for MMP
	void * addr = 0;
	int protections = PROT_READ|PROT_WRITE; //read + write
	int flags = MAP_SHARED|MAP_ANONYMOUS; //it is shared between processes and does not map to a file
	int fd = - 1; //We could make it map to a file as well but here it is not 
	int offset = 0;

	//create memory map

	Bank * accounts = mmap(addr, size ,protections, flags, fd, offset);

	if( (void *) ERROR == accounts)
	{
		perror("error with mmap");
		exit(EXIT_FAILURE);
	}

	return accounts;

}

pid_t forkChild( void (* function) (void *, Repo *, Bank *), void * ptr, Repo * repo, Bank * accounts)
{
	//this funciton takes a point to a function as an arg
	//and said functions argument, it then returns the forked child's pid.

	pid_t childpid;
	int fd = *((int*)ptr);
	if(fd == -1){

		return -1;
	}

	switch ( childpid = fork() )
	{
		case ERROR:
			return -1;
		case 0:
			( * function)(ptr, repo, accounts);
			if( childpid == 0)
				exit(0);
		default:
			return childpid;
	}
}

Repo * createMMAP ( size_t size )
{

	//neccessary arguments for MMP
	void * addr = 0;
	int protections = PROT_READ|PROT_WRITE; //read + write
	int flags = MAP_SHARED|MAP_ANONYMOUS; //it is shared between processes and does not map to a file
	int fd = - 1; //We could make it map to a file as well but here it is not 
	int offset = 0;

	//create memory map

	Repo * map = mmap(addr, size ,protections, flags, fd, offset);

	if( (void *) ERROR == map)
	{
		perror("error with mmap");
		exit(EXIT_FAILURE);
	}

	return map;

}

//take a wild guess? lolol
void deleteBank ( void * addr )
{
	if( ERROR == munmap ( addr, sizeof( Bank ) * 20 ) )
	{
		perror("error delete mmap");
		exit(EXIT_FAILURE);
	}

	return;
}

void deleteMMAP ( void * addr )
{
	if( ERROR == munmap ( addr, sizeof( Repo) * 20 ) )
	{
		perror("error delete mmap");
		exit(EXIT_FAILURE);
	}

	return;
}

int claim_port( const char * port )
{
	struct addrinfo		addrinfo;
	struct addrinfo *	result;
	int			sd;
	char			message[256];
	int	on = 1; //

	addrinfo.ai_flags = AI_PASSIVE;		// for bind()
	addrinfo.ai_family = AF_INET;		// IPv4 only
	addrinfo.ai_socktype = SOCK_STREAM;	// Want TCP/IP
	addrinfo.ai_protocol = 0;		// Any protocol
	addrinfo.ai_addrlen = 0;
	addrinfo.ai_addr = NULL;
	addrinfo.ai_canonname = NULL;
	addrinfo.ai_next = NULL;
	
	//calls setup functions + error checking 
	// ( NODE    SERVICE      HINT    RES )

	if ( getaddrinfo( 0, port, &addrinfo, &result ) != 0 )		// want port 3000
	{

		/*
		 NODE = POINTER TO HOST, NULL = LOCAL HOST 127.0.0.1
		
		 SERVICE = PORT 

		 HINT = addrinfo struct with parameters relating to SERVICE
		
		 RES = addrinfo struct pointer which returns 1 or more structs storing 
		 system information relating to the function call.

		 */

		fprintf( stderr, "\x1b[1;31mgetaddrinfo( %s ) failed errno is %s.  File %s line %d.\x1b[0m\n", port, strerror( errno ), __FILE__, __LINE__ );
		return -1;
	}
	else if ( errno = 0, (sd = socket( result->ai_family, result->ai_socktype, result->ai_protocol )) == -1 )
	{

		/*

		CREATE A SOCKET FILE DESCRIPTOR 
			Integer that points to a FD that shares kernel level information for communcation

			result->ai_family = IPv4

			result->ai_socktype = SOCK_STREAM = 2 way communication via byte streams

			result->ai_protocol = 0 = any

		 SOCKET = IP:PORT || HOST:SERVICE 
		 	255.255.255.255:65535

		 Server3.c (bank) 
		 	SOCKET = 127.0.0.1:3000 (will change to safus port ordering)

	 	PACKET  
	 		TO:FROM
	 			DESTINATION IP: SOURCE IP
	 			DESTINATION PORT: SOURCE PORT

		*/

		write( 1, message, sprintf( message, "socket() failed.  File %s line %d.\n", __FILE__, __LINE__ ) );
		freeaddrinfo( result );
		return -1;
	}
	else if ( setsockopt( sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on) ) == -1 )
	{

		/*

		setsockpot = allows for manipulation of different levels of socket 
		descriptor options, in this call we're setting SO_REUSEADDR to true
		
		sd = socket descriptor (the socket we created above)

		SOL_SOCKET = Maniuplate socket level options(the next arugment) 

		SO_REUSEADDR = allow the reuse of local addresses for bind() i.e. duplicate addresses 

		on = what were setting SO_REUSEADDER to, so 1 is "true" or "on", 0 would be false

		 */
		write( 1, message, sprintf( message, "setsockopt() failed.  File %s line %d.\n", __FILE__, __LINE__ ) );
		freeaddrinfo( result );
		close( sd );
		return -1;
	}
	else if ( bind( sd, result->ai_addr, result->ai_addrlen ) == -1 )
	{
		/*

			a socket descriptor created via socket() it has no address
			specifed to it. Bind users an addr struct to asign an address
			to an object. 

			sd = socket descriptor 

			result->ai_addr = pointer to socket address

			result->ai_addrlen = length of socket addess in bytes

		
		 */

		freeaddrinfo( result );

		/*
		
			frees dynamically allocated memory associated with the linked 
			list in the "res" arguemnt of socket.

		 */

		close( sd ); //self explaintory... WHAT A C FUNCTION THAT DOESN'T HAVE A STUPID NAME!?!?!
		write( 1, message, sprintf( message, "\x1b[2;33mBinding to port %s ...\x1b[0m\n", port ) );
		return -1;
	}
	else
	{
		write( 1, message, sprintf( message,  "\x1b[1;32mSUCCESS : Bind to port %s\x1b[0m\n", port ) );
		freeaddrinfo( result );		
		return sd;			// bind() succeeded;
	}
}