#include <stdio.h>
#include <stdlib.h>
#include "types.h"
#include "tlb.h"
#include "cpu.h"
#include "mmu.h"

/*
 * Programming Assignment 2
 * Operating Systems UA-202
 * Alvin Mayers
 */

/* I defined the TLB as an array of entries,
   each containing the following:
   Valid bit: 1bit
   Virtual Page: 21 bits
   Reference bit: 1 bit
   Modified bit: 1 bit
   Page Frame: 21 bits
*/

//NOTE: REFER TO tlb.h FOR DETAILED DESCRIPTION OF EACH FUNCTION

/************************CONSTANTS, MACROS AND STRUCTS***********************************/ 
const int BIT_NUM=31;
const int RBIT_NUM=20;

typedef struct {
  unsigned int vbit_and_vpage;  // 32 bits containing the valid bit and the 21-bit
                                // virtual page number.
  unsigned int mr_pframe;       // 32 bits containing the modified bit, reference bit,
                                // and 21-bit page frame number
} TLB_ENTRY;

TLB_ENTRY *tlb;

unsigned int num_tlb_entries;

BOOL tlb_miss;  //this is set to TRUE when there is a tlb miss;

#define OFF 0
#define ON 1

#define VBIT_MASK   0x80000000  //VBIT is leftmost bit of first word 
#define VPAGE_MASK  0x001FFFFF  //lowest 21 bits of first word 
#define MBIT_MASK   0x80000000  //MBIT is leftmost bit of second word
#define RBIT_MASK   0x40000000  //RIT is second leftmost bit of second word 
#define PFRAME_MASK 0x001FFFFF  //lowest 21 bits of second word

 int next_vpage_to_check; 

/**********************EXTRA FUNCTIONS (NOT IN HEADER FILE)***********************************/ 
 void set_bit(int bit_type,int bit_index, unsigned int* val){
	if(bit_type==OFF)
		*val= *val & (~(1<< bit_index));
	else if(bit_type==ON){
		*val= *val | (1<<bit_index);	
	} 
}
void write_rm_bit(int val){
	mmu_modify_rbit_in_bitmap((val & PFRAME_MASK),(val & RBIT_MASK));
	mmu_modify_mbit_in_bitmap((val & PFRAME_MASK),(val & MBIT_MASK));
}
int check_fit(int index_a,int index_b){
	int i;
	for (i = index_a; i < index_b; ++i)
	{
		if(((tlb[i].vbit_and_vpage & VBIT_MASK)==0) || ((tlb[i].vbit_and_vpage & RBIT_MASK)==0)){
			if(tlb[i].vbit_and_vpage & VBIT_MASK){
				next_vpage_to_check=i;
				write_rm_bit(tlb[i].mr_pframe);
			}
			return 1;
		}
	}
	return 0;
}
 /*********************FUNCTIONS INCLUDED IN HEADER FILE******************************************/
void tlb_initialize()
{                  
  tlb = (TLB_ENTRY *) malloc(num_tlb_entries * sizeof(TLB_ENTRY));
  next_vpage_to_check = 0;			
  tlb_miss=0;
  int i;
  for (i = 0; i < num_tlb_entries; i++)
  {
	  tlb[i].vbit_and_vpage=0;
	  tlb[i].mr_pframe=0;
  }
}
void tlb_clear_all() 
{
  int k;
  for (k = 0; k < num_tlb_entries; k++)
	  set_bit(OFF,BIT_NUM,&tlb[k].vbit_and_vpage);
}
void tlb_clear_R_bits() 
{
   int k;
   for (k = 0; k < num_tlb_entries; k++)
  	tlb[k].mr_pframe &= (~RBIT_MASK);
}
void tlb_clear_entry(VPAGE_NUMBER vpage)
{
  	int k;
	for (k = 0; k < num_tlb_entries; ++k)
	{
		if(vpage==(tlb[k].vbit_and_vpage & VPAGE_MASK))
			set_bit(OFF,BIT_NUM,&tlb[k].vbit_and_vpage);
	}
}
PAGEFRAME_NUMBER tlb_lookup_vpage(VPAGE_NUMBER vpage, OPERATION op)
{
  	int i;
	for (i = 0; i < num_tlb_entries; ++i)
	{
		if(tlb[i].vbit_and_vpage & VBIT_MASK)
			if((tlb[i].vbit_and_vpage & VPAGE_MASK)==vpage){
				tlb_miss=0;
				set_bit(ON,BIT_NUM-1,&tlb[i].mr_pframe); //set reference bit
				if(op==STORE) 
					set_bit(ON,BIT_NUM,&tlb[i].mr_pframe); //set modified bit
				return (tlb[i].mr_pframe & PFRAME_MASK);
			}
	}
	tlb_miss=1;
}
void tlb_insert_vpage(VPAGE_NUMBER new_vpage, PAGEFRAME_NUMBER new_pframe,
		BOOL new_rbit, BOOL new_mbit)
{
 
	if(!check_fit(next_vpage_to_check,num_tlb_entries))
		check_fit(0,next_vpage_to_check);

	//insert new vpage and set valid bit
	tlb[next_vpage_to_check].vbit_and_vpage=0;
	tlb[next_vpage_to_check].vbit_and_vpage=new_vpage;
	set_bit(ON,BIT_NUM,&tlb[next_vpage_to_check].vbit_and_vpage);

	//insert new page frame # and set modified bit and reference bit
	tlb[next_vpage_to_check].mr_pframe=0;
	tlb[next_vpage_to_check].mr_pframe=new_pframe;
	set_bit(ON,BIT_NUM,&tlb[next_vpage_to_check].mr_pframe);
	set_bit(ON,BIT_NUM-1,&tlb[next_vpage_to_check].mr_pframe);
	
	if(1+next_vpage_to_check <num_tlb_entries){
		next_vpage_to_check++;
		return;
	}
	next_vpage_to_check=0;
}
void tlb_write_back_r_m_bits()
{	
   	int i;
	for (i = 0; i < num_tlb_entries; ++i)
	{
		if((tlb[i].vbit_and_vpage & VBIT_MASK)){
			int val=tlb[i].mr_pframe;
			write_rm_bit(val);
		}
	}
}
