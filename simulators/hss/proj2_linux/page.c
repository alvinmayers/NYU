#include <stdio.h>
#include <stdlib.h>
#include "types.h"
#include "mmu.h"
#include "page.h"
#include "cpu.h"

/* Alvin Mayers
 * Programming Assignment #3
 * OS UA-202
 */
/**************CONSTANTS, GLOBAL VARIABLES, MACROS*******************************/

#define VBIT_MASK   0x80000000  
#define PFRAME_MASK 0x001FFFFF  

typedef unsigned int PT_ENTRY;

PT_ENTRY **first_level_page_table;

BOOL page_fault; 

const int NUM_FL_ENTRIES=2048;
const int NUM_SL_ENTRIES=1024;
const int OFFSET=11;

#define FL_MASK   0x000007FF 
#define SL_MASK   0x001FF800 

/*************************FUNCTIONS SPECIFIED BY HEADER***************************/

void pt_initialize_page_table()
{
	 first_level_page_table = (PT_ENTRY **) malloc(NUM_FL_ENTRIES* sizeof(PT_ENTRY*));
	 int i; for(i=0; i<NUM_FL_ENTRIES;i++) first_level_page_table[i]=NULL;
}
PAGEFRAME_NUMBER pt_get_pframe_number(VPAGE_NUMBER vpage)
{
	 	if (first_level_page_table[vpage & FL_MASK]==NULL){
	 		page_fault=TRUE;
			return;
		}
		else if(((first_level_page_table[vpage & FL_MASK][(vpage & SL_MASK)>>OFFSET]) & VBIT_MASK)==0){
			page_fault=TRUE;
			return;
		}
		page_fault=FALSE; 
		return ((first_level_page_table[vpage & FL_MASK][(vpage & SL_MASK)>>OFFSET]) & PFRAME_MASK);
}
void pt_update_pagetable(VPAGE_NUMBER vpage, PAGEFRAME_NUMBER pframe)
{
	 	if (first_level_page_table[vpage & FL_MASK]==NULL)
			first_level_page_table[vpage & FL_MASK] =(PT_ENTRY *)calloc(NUM_SL_ENTRIES,sizeof(PT_ENTRY));
		
		first_level_page_table[vpage & FL_MASK][(vpage & SL_MASK)>>OFFSET]|=VBIT_MASK;
		first_level_page_table[vpage & FL_MASK][(vpage & SL_MASK)>>OFFSET]=pframe;
}
void pt_clear_page_table_entry(VPAGE_NUMBER vpage)
{
	 	if (first_level_page_table[vpage & FL_MASK]!=NULL)
			first_level_page_table[vpage & FL_MASK][(vpage & SL_MASK)>>OFFSET] &= (~VBIT_MASK);
}
/*****************************END OF FILE*************************************/
