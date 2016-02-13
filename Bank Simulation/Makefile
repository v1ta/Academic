
CFLAGS= -pthread  -std=gnu99
CFLAGS2=  -pthread -std=gnu99

all: client server

client: client.o bank_client.o  
	gcc $(CFLAGS2) client.o bank_client.o -o client

server: bank_server.o server.o 
	gcc $(CFLAGS) server.o bank_server.o -o server

bank_client.o: bank_client.c
	gcc $(CFLAGS) -c bank_client.c -o bank_client.o

bank_server.o: bank_server.c
	gcc $(CFLAGS) -c bank_server.c -o bank_server.o

server.o: server.c
	gcc $(CFLAGS) -c server.c -o server.o

client.o: client.c
	gcc $(CFLAGS) -c client.c -o client.o

clean:
	rm -f client
	rm -f server
	rm -f *.o
