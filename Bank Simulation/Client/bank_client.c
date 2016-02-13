#include "bank_client.h"

char prompt[512];
char prompt2[512];
char input_to_send[512];
char message[512];
char sent_input[512];
char buffer[512];
char output[512];
int	len;
int	count;



int connect_to_server( const char * server, const char * port )
{
	int			sd;
	struct addrinfo		addrinfo;
	struct addrinfo *	result;
	char			message[256];
	printf("SERVER: %s\n\n", server);
	printf("PORT: %s\n\n", port);

	addrinfo.ai_flags = 0;
	addrinfo.ai_family = AF_INET;		// IPv4 only
	addrinfo.ai_socktype = SOCK_STREAM;	// Want TCP/IP
	addrinfo.ai_protocol = 0;		// Any protocol
	addrinfo.ai_addrlen = 0;
	addrinfo.ai_addr = NULL;
	addrinfo.ai_canonname = NULL;
	addrinfo.ai_next = NULL;
	if ( getaddrinfo( server, port, &addrinfo, &result ) != 0 )
	{
		fprintf( stderr, "\x1b[1;31mgetaddrinfo( %s ) failed.  File %s line %d.\x1b[0m\n", server, __FILE__, __LINE__ );
		return -1;
	}
	else if ( errno = 0, (sd = socket( result->ai_family, result->ai_socktype, result->ai_protocol )) == -1 )
	{
		freeaddrinfo( result );
		return -1;
	}
	else
	{
		do {
			if ( errno = 0, connect( sd, result->ai_addr, result->ai_addrlen ) == -1 )
			{
				sleep( 1 );
				write( 1, message, sprintf( message, "\x1b[2;33mConnecting to server %s ...\x1b[0m\n", server ) );
			}
			else
			{
				freeaddrinfo( result );
				return sd;		// connect() succeeded
			}
		} while ( errno == ECONNREFUSED );
		freeaddrinfo( result );
		return -1;
	}
}


int account_client_session( )
{
	fseek(stdin,0,SEEK_END); //clear input buffer
	sprintf( prompt2, "\n1. Deposit\n2. Withdraw\n3. Query\n4. End\n\n" );

	INPUT:
	write( 1, prompt2, sizeof( prompt2 ));
	read( 1, input_to_send, 1);
	char option = input_to_send[0];
	switch(option){

		case '1':
			write(1, "Deposit: ", 9);
			len = read( 0, input_to_send, sizeof(input_to_send) );
			break;
		case '2':
			write(1, "Withdraw: ", 10);
			len = read( 0, input_to_send, sizeof(input_to_send) );
			break;
		case '3':
			write(1, "Query...\n", 9);
			strncpy(input_to_send, "3",1);
			break;
		case '4':
			write(1, "End...\n", 7);
			break;
		default:
			write(1, "ERROR: invalid input\n\n", 22);
			goto INPUT;
			}

	len = read( 0, input_to_send, sizeof(input_to_send) );
	input_to_send[len] = option;
	input_to_send[len+1] = '\0';

	return len;

}


int general_client_session()
{	
	fseek(stdin,0,SEEK_END); // clear input buffer
	sprintf( prompt, "\nSelection a command:\n1. Create Account\n2. Serve Account\n3. Quit\n\n");
	INPUT:
	write( 1, prompt, sizeof( prompt ));
	read( 1, input_to_send, 1);
	char option = input_to_send[0];
	switch(option){

		case '1':
			write(1, "Create: ", 8);
			len = read( 0, input_to_send, sizeof(input_to_send) );
			break;
		case '2':
			write(1, "Serve: ", 7);
			len = read( 0, input_to_send, sizeof(input_to_send) );
			break;
		case '3':
			close_client();
			break;
		default:
			write(1, "ERROR: invalid input\n\n", 22);
			goto INPUT;

	}
	
	len = read( 0, input_to_send, sizeof(input_to_send) );
	input_to_send[len - 1] = option;
	input_to_send[len] = '\0';
	

	return len;
}

void * talk_to_server(void * arg)
{
	// start communication to server, do not allow user to change input until 
	// the server has responded to the command.

    //pthread_mutex_lock(&lock);
    //input 
    
	int session = 0;

    while( 1 ){

    	session ? account_client_session() : general_client_session();
    	system ("/bin/stty raw");
    	sleep(2);
    	write( sd, input_to_send, strlen( input_to_send ) + 1 );
		INPUT:
		if ( read( sd, buffer, sizeof(buffer)) < 1 ){
			printf("Lost connection to server\n");

		}else if(buffer[strlen(buffer) - 1] == '~'){
			buffer[strlen(buffer) - 1] = '\0';
			if(session == 0){
				session = 1;
			}else if(session == 1){
				session = 0;
			}
		}
		else if(buffer[strlen(buffer) - 1] == '&'){
			buffer[strlen(buffer) - 1] = '\0';
			printf("\nSERVER: %s\n", buffer);
			system ("/bin/stty raw");
			memset(&buffer[0], 0, sizeof(buffer));
			system ("/bin/stty cooked");
			goto INPUT;

		}
		printf("\nSERVER: %s\n", buffer);
		memset(&buffer[0], 0, sizeof(buffer));
		system ("/bin/stty cooked");
		
	}

	close_client();
    return NULL;
}



void in_session(int session){

	if(session)
		session = 0;
	else if(!session)
		session = 1;

}


void close_client(){

	close(sd);
	system ("/bin/stty cooked");
	_exit(0);

}

