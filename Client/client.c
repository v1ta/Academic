
#include "bank_client.h"


pthread_t comm;
pthread_mutex_t lock;
int sd;
char message[512];
int client;

static void signal_handler( int signo )
{

	client = 0;
	close_client();

}

int main( int argc, char ** argv )
{
	// Signal handler SIGINT

	struct sigaction action;

	action.sa_flags = 0;
	action.sa_handler = signal_handler;	
	sigemptyset( &action.sa_mask );		

	sigaction( SIGINT, &action, 0 );
	
	//Connection 
	if ( argc < 2 )
	{
		fprintf( stderr, "\x1b[1;31mNo host name specified.  File %s line %d.\x1b[0m\n", __FILE__, __LINE__ );
		exit( 1 );
	}
	else if ( (sd = connect_to_server( argv[1], "49926" )) == -1 )
	{
		write( 1, message, sprintf( message,  "\x1b[1;31mCould not connect to server %s errno %s\x1b[0m\n", argv[1], strerror( errno ) ) );
		return 1;
	}

	if ( pthread_mutex_init(&lock, NULL) != 0 )
    {
        printf("\n mutex init failed\n");
        return 1;
    }

	printf( "Connected to server %s\n", argv[1] );
	while ( sd > 0 )
	{
		
		//if(pthread_create(&user, NULL, &talk_to_user, NULL) != 0)
		//{
		//	printf("\ncan't create thread");
		//}

		//pthread_join(user, NULL);

		//strncpy( sent_input, input_to_send, 512);
        if (pthread_create(&comm, NULL, &talk_to_server, NULL) != 0)
        {
            printf("\ncan't create thread");
        }
		
        pthread_join(comm, NULL);

	}
	close( sd );
		
	
	printf("\nDisconnected from server\n");

	return 0;
}