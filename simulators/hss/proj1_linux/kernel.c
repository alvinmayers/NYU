#include <stdio.h>
#include <stdlib.h>
#include "hardware.h"
#include "drivers.h"
#include "kernel.h"

/***************
 * Alvin Mayers
 * OS UA-202
 * assignment #1
 ***************/

#define INITIAL_SEMAPHORE_VALUE 1;

#define DOWN 0

#define UP 1

#define QUANTUM 40

#define NUMBER_OF_SEMAPHORES 16

#define NUMBER_OF_INTERRUPTS 4


/***********************************************************************/
typedef enum { RUNNING, READY, BLOCKED , UNINITIALIZED } PROCESS_STATE;

typedef struct process_table_entry {
  PROCESS_STATE state;
  int total_CPU_time_used;
} PROCESS_TABLE_ENTRY;

typedef struct PID_queue_elt {
  struct PID_queue_elt *next;
  PID_type pid;
} PID_QUEUE_ELT;


typedef struct {
  PID_QUEUE_ELT *head;
  PID_QUEUE_ELT *tail;
} PID_QUEUE;


typedef struct SEMAPHORE_TABLE_ENTRY{
	PID_type val;
	PID_QUEUE *SQ;
} SEMAPHORE_TABLE_ENTRY; 

/***********************************************************************/
PROCESS_TABLE_ENTRY process_table[MAX_NUMBER_OF_PROCESSES];

SEMAPHORE_TABLE_ENTRY semaphore_table[NUMBER_OF_SEMAPHORES];

FN_TYPE INTERRUPT_TABLE[NUMBER_OF_INTERRUPTS];

PID_type current_pid;

int current_quantum_start_time;

int process_interval;


PID_QUEUE* RQ; //ready queue

int disk_requests;

int key_requests;

int idle_flag;
/***********************************************************************/
void enqueue(PID_QUEUE* Q,PID_type Q_pid){
	PID_QUEUE_ELT* nQ=(PID_QUEUE_ELT*)malloc(sizeof(PID_QUEUE_ELT));
	nQ->pid=Q_pid;
	if(Q->head!=NULL){
		Q->tail->next=nQ;
		Q->tail=Q->tail->next;
		Q->tail->next=NULL;
		return;
	}
	Q->tail=nQ;
	Q->head=nQ;
}
PID_type dequeue(PID_QUEUE* Q){
	PID_type Q_pid;
	if(Q->head!=NULL){
		Q_pid=Q->head->pid;
		Q->head=Q->head->next;
		return Q_pid;
	}
	return -1;
}

/***********************************************************************/
int IO_requests(){
	if(disk_requests==0 & key_requests==0)
		return 0;
	return 1;
}
/***********************************************************************/
void print_queue();
void process_time();
void nextProc(PID_QUEUE* Q){

	current_quantum_start_time=clock;
	process_interval=current_quantum_start_time;

	current_pid=dequeue(Q); //take new process of ready queue
	
	if(current_pid!=IDLE_PROCESS) idle_flag=0;

	else if((current_pid==IDLE_PROCESS) & (!IO_requests())){ //if there are no more processes exit
		int dl_flag=0;
		int i;
		for(i=0;i<NUMBER_OF_SEMAPHORES;i++){
			if(semaphore_table[i].SQ->head!=NULL)
				dl_flag++;
		}
		if(dl_flag>0)
			printf("DeadLock System\n");
		else
			printf("No more Processes to execute\n");
		exit(0);
	}
	else if((current_pid==IDLE_PROCESS) & (IO_requests()) & (idle_flag!=1)) {
		printf("Time %d: processor is idle\n",clock);
		idle_flag=1;
		return;
	}
	process_table[current_pid].state=RUNNING;
	if(current_pid!=IDLE_PROCESS)
		printf("Time %d: Process %d runs\n",clock,current_pid);
}
/***********************************************************************/
void process_time();
void S_down(int sid){
	printf("Time %d: Process %d issues DOWN on semaphore %d\n",clock,current_pid,sid);
	if(semaphore_table[sid].val==0){
		process_time();
		process_table[current_pid].state=BLOCKED;
		enqueue(semaphore_table[sid].SQ,current_pid);
		nextProc(RQ);
		}
	else if(semaphore_table[sid].val==1) semaphore_table[sid].val--;
}

void S_up(int sid){
	printf("Time %d: Process %d issues UP on semaphore %d\n",clock,current_pid,sid);
	if(semaphore_table[sid].val==0){
		PID_type next_proc=dequeue(semaphore_table[sid].SQ);	
		if(next_proc!=-1){
			process_table[next_proc].state=READY;
			enqueue(RQ,next_proc);
		}
	}
	else semaphore_table[sid].val++;
}

/***********************************************************************/
void process_time(){
	process_table[current_pid].total_CPU_time_used+=(clock-process_interval);
	process_interval=clock;
}
/***********************************************************************/
void print_queue(){
	PID_QUEUE_ELT* itr=RQ->head;
	printf("queue: ");
	while(itr!=NULL){
		printf("%d,",itr->pid);
		itr=itr->next;
	}
	printf("\n");
}
/***********************************************************************/
void trap_interrupt(){
	switch(R1){
		case DISK_READ:
			disk_read_req(current_pid,R2);
			process_time();
			disk_requests++;
			process_table[current_pid].state=BLOCKED;
			printf("Time %d: Process %d issues disk read request\n",clock,current_pid);
			nextProc(RQ);
			break;			
		case DISK_WRITE:
			disk_write_req(current_pid);
			printf("Time %d: Process %d issues disk write request\n",clock,current_pid);
			break;
		case KEYBOARD_READ:
			keyboard_read_req(current_pid);
			process_time();
			process_table[current_pid].state=BLOCKED;
			key_requests++;
			printf("Time %d: Process %d issues keyboard read request\n",clock,current_pid);
			nextProc(RQ);
			break;
		case FORK_PROGRAM:
			process_table[R2].state=READY; //set created process to ready
			enqueue(RQ,R2); //place newly create process on ready queue
			printf("Time %d: Creating process entry for pid %d\n",clock,R2);
			break;
		case END_PROGRAM:
			process_time();
			process_table[current_pid].state=UNINITIALIZED;
			printf("Time %d: End process %d, Total CPU Time: %d\n",
					clock,current_pid,process_table[current_pid].total_CPU_time_used);
			process_table[current_pid].total_CPU_time_used=0;
			nextProc(RQ);
			break;
		case SEMAPHORE_OP:
			switch(R3){
				case DOWN:
					S_down(R2);
					break;
				case UP:
					S_up(R2);
					break;
			}
			break;
	}
}
void clock_interrupt(){
	process_time();
	if(clock-current_quantum_start_time>=QUANTUM)
	{
		if(current_pid!=IDLE_PROCESS){
			process_table[current_pid].state=READY;
			enqueue(RQ,current_pid);
		}
		nextProc(RQ);
	}
}
void disk_interrupt(){
	process_table[R1].state=READY;
	enqueue(RQ,R1);
	if(current_pid==IDLE_PROCESS)
		nextProc(RQ);
	printf("Time %d: Handled disk interrupt for pid %d\n",clock,R1);
	disk_requests--;

}
void keyboard_interrupt(){
	process_table[R1].state=READY;
	enqueue(RQ,R1);
	if(current_pid==IDLE_PROCESS)
		nextProc(RQ);
	printf("Time %d: Handled keyboard interrupt for pid %d\n",clock,R1);
	key_requests--;
}
/***********************************************************************/

void initialize_kernel()
{
	//set current_pid to 0
	current_pid=0;

	//start quantum interval from t=0
	current_quantum_start_time=0;

	process_interval=0;

	//start process 0
	process_table[current_pid].total_CPU_time_used=0;
	process_table[current_pid].state=RUNNING;
	
	//set remaining processes to uninitialized
	int i;
	for(i=1;i<MAX_NUMBER_OF_PROCESSES;i++){
		process_table[i].state=UNINITIALIZED;
		process_table[i].total_CPU_time_used=0;
	} 
	
	//initialize semaphores to default and their queues	
	int j;
	for(j=0;j<NUMBER_OF_SEMAPHORES;j++){
		semaphore_table[j].val=INITIAL_SEMAPHORE_VALUE;
		semaphore_table[j].SQ=(PID_QUEUE*)malloc(sizeof(PID_QUEUE));
		semaphore_table[j].SQ->head=NULL;
		semaphore_table[j].SQ->tail=NULL;
	}
	//setup interrupt_table
	INTERRUPT_TABLE[TRAP]=trap_interrupt;
	INTERRUPT_TABLE[CLOCK_INTERRUPT]=clock_interrupt;
	INTERRUPT_TABLE[DISK_INTERRUPT]=disk_interrupt;
	INTERRUPT_TABLE[KEYBOARD_INTERRUPT]=keyboard_interrupt;

	//setup ready queue
	RQ=(PID_QUEUE*)malloc(sizeof(PID_QUEUE));
	RQ->head=NULL;
	RQ->tail=NULL;

	//set outstanding io requests to 0
	disk_requests=0;
	key_requests=0;

	idle_flag=0;
}
