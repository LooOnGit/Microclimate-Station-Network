/*

    [*] WRITE BY BUI VAN HUY
		[*] 28/11/2019

*/
#include "flash.h"

//__static_funtion___
static void Flash_Erase(uint32_t addr);
static void deleteBuffer8(uint8_t* data, uint16_t _LENGTH_);
static void deleteBuffer16(uint16_t* data, uint16_t _LENGTH_);
static void deleteBuffer32(uint32_t* data, uint16_t _LENGTH_);

//______________________________________________________________________________________________________



//______________________________________________________________________________________________________
/*
     - The function writes an integer variable to the flash memory
     - _DATA_         : an integer variable
     -_ADDRESS_DATA_  : page address 
*/
void  Flash_Write_Uint(uint16_t _DATA_, uint32_t _ADDRESS_DATA_)
{   
	//___disable_systick___
	  /*
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	    uint32_t tick_var = uwTick;
	   */
	      HAL_FLASH_Unlock();
	      Flash_Erase(_ADDRESS_DATA_);
	      SET_BIT(FLASH->CR, FLASH_CR_PG);
          while((FLASH->SR&FLASH_SR_BSY)){};
          *(__IO uint16_t*)(_ADDRESS_DATA_) = (uint16_t)(_DATA_ & 0xFFFF) ;
						while((FLASH->SR&FLASH_SR_BSY)){};
          *(__IO uint16_t*)(_ADDRESS_DATA_ + 2U) = (uint16_t)((_DATA_ >> 16U) & 0xFFFF);
			
	while((FLASH->SR&FLASH_SR_BSY)){};
		     CLEAR_BIT(FLASH->CR , FLASH_CR_PG);
         HAL_FLASH_Lock();
		
		//___enable_systick___
		/*
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
		*/
		
}
//________________________________________________________________________________________

//________________________________________________________________________________________


/*
     - The function writes a String to flash memory
     - _Array_DATA_         : String / 8 bit array address
     -_ADDRESS_DATA_        : page address 
     -_LENGTH_              : length of array
*/
void  Flash_Write_String(uint8_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_)
{   
	//___disable_systick___
	  /*
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	    uint32_t tick_var = uwTick;
	   */
	      HAL_FLASH_Unlock();
	      //Flash_Erase(_ADDRESS_DATA_);
	      SET_BIT(FLASH->CR, FLASH_CR_PG);

			for(uint16_t i=0; i < _LENGTH_; i++)
			{  
          while((FLASH->SR&FLASH_SR_BSY)){};
          *(__IO uint16_t*)(_ADDRESS_DATA_ + i*2U) = _Array_DATA_[i];
			}
	while((FLASH->SR&FLASH_SR_BSY)){};
		     CLEAR_BIT(FLASH->CR , FLASH_CR_PG);
         HAL_FLASH_Lock();
		
		//___enable_systick___
		/*
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
		*/
		
}

//______________________________________________________________________________________________________

//______________________________________________________________________________________________________


/*
     - The function writes a 16-bit array to flash memory
     - _Array_DATA_         : 16 bit array address
     -_ADDRESS_DATA_        : page address 
     -_LENGTH_              : length of array
*/
void  Flash_Write_Array_16bit(uint16_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_)
{   
	//___disable_systick___
	  /*
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	    uint32_t tick_var = uwTick;
	   */
	      HAL_FLASH_Unlock();
	      Flash_Erase(_ADDRESS_DATA_);
	      SET_BIT(FLASH->CR, FLASH_CR_PG);

			for(uint16_t i=0; i < _LENGTH_; i++)
			{  
          while((FLASH->SR&FLASH_SR_BSY)){};
						
          *(__IO uint16_t*)(_ADDRESS_DATA_ + i*2U) = (uint16_t)_Array_DATA_[i];
			}
	while((FLASH->SR&FLASH_SR_BSY)){};
		     CLEAR_BIT(FLASH->CR , FLASH_CR_PG);
         HAL_FLASH_Lock();
		
		//___enable_systick___
		/*
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
		*/
		
}

//______________________________________________________________________________________________________


//______________________________________________________________________________________________________
/*
     - The function writes a 32-bit array to flash memory
     - _Array_DATA_         : 32 bit array address
     -_ADDRESS_DATA_        : page address 
     -_LENGTH_              : length of array
*/
void  Flash_Write_Array_32bit(uint32_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_)
{   
	//___disable_systick___
	  /*
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	    uint32_t tick_var = uwTick;
	   */
	      HAL_FLASH_Unlock();
	      Flash_Erase(_ADDRESS_DATA_);
	      SET_BIT(FLASH->CR, FLASH_CR_PG);

			for(uint16_t i=0; i < _LENGTH_; i++)
			{  
          while((FLASH->SR&FLASH_SR_BSY)){};
          *(__IO uint16_t*)(_ADDRESS_DATA_ + i*4U) = (uint16_t)(_Array_DATA_[i] & 0xFFFF) ;
						while((FLASH->SR&FLASH_SR_BSY)){};
          *(__IO uint16_t*)(_ADDRESS_DATA_ + i*4U + 2U) = (uint16_t)((_Array_DATA_[i] >> 16U) & 0xFFFF);
			}
	while((FLASH->SR&FLASH_SR_BSY)){};
		     CLEAR_BIT(FLASH->CR , FLASH_CR_PG);
         HAL_FLASH_Lock();
		
		//___enable_systick___
		/*
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
		*/
		
}
//______________________________________________________________________________________________________


//______________________________________________________________________________________________________
/*
     - The function reads an integer variable from the flash memory
     - _DATA_         : an integer variable
     -_ADDRESS_DATA_  : page address 
*/
uint32_t Flash_Read_Uint(uint32_t _ADDRESS_DATA_)
{ 
	//___disable_systick___
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	     uint32_t tick_var = uwTick;
	
	uint32_t val = (*(uint16_t*)(_ADDRESS_DATA_)) | ((*(uint16_t*)(_ADDRESS_DATA_ + 2U))<<16U);
	
	//___enable_systick___
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	
	return val;
}
//______________________________________________________________________________________________________


//______________________________________________________________________________________________________

/*
     - The function reads a String array from flash memory
     - _Array_DATA_         : String / 8 bit array address
     -_ADDRESS_DATA_        : page address 
     -_LENGTH_              : length of array
*/
void Flash_Read_String(uint8_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_)
{     //___disable_systick___
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	     uint32_t tick_var = uwTick;
	
	 //_write_flash
	   HAL_FLASH_Unlock();
     	deleteBuffer8(_Array_DATA_,_LENGTH_);
				 for(uint16_t i = 0; i < _LENGTH_; i++)
				    _Array_DATA_[i] = *(uint8_t *)(_ADDRESS_DATA_ + (uint32_t)(i*2U));
	   HAL_FLASH_Lock();
	//___enable_systick___
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
			 
			 
}
//______________________________________________________________________________________________________


//______________________________________________________________________________________________________

/*
     - The function reads a 16-bit array from flash memory
     - _Array_DATA_         : 16 bit array address
     -_ADDRESS_DATA_        : page address 
     -_LENGTH_              : length of array
*/
void Flash_Read_Array_16bit(uint16_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_)
{     //___disable_systick___
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	     uint32_t tick_var = uwTick;
	
	 //_write_flash
	   HAL_FLASH_Unlock();
     	deleteBuffer16(_Array_DATA_,_LENGTH_);
				 for(uint16_t i = 0; i < _LENGTH_; i++)
				    _Array_DATA_[i] = *(uint16_t *)(_ADDRESS_DATA_ + (uint32_t)(i*2U));
	   HAL_FLASH_Lock();
	//___enable_systick___
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
			 
			 
}
//______________________________________________________________________________________________________

//______________________________________________________________________________________________________
/*
     - The function reads a 32-bit array from flash memory
     - _Array_DATA_         : 32 bit array address
     -_ADDRESS_DATA_        : page address 
     -_LENGTH_              : length of array
*/
void Flash_Read_Array_32bit(uint32_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_)
{     //___disable_systick___
     	 CLEAR_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);
	     HAL_TickFreqTypeDef tick_freq =   uwTickFreq;
	     uint32_t tick_var = uwTick;
	
	 //_write_flash
	   deleteBuffer32(_Array_DATA_,_LENGTH_);
				 for(uint16_t i = 0; i < _LENGTH_; i++)
	         {
						 /*
						 uint32_t left    =   *(uint16_t*)(_ADDRESS_DATA_ + (uint32_t)(i*4U));
					   uint32_t right   =   *(uint16_t*)(_ADDRESS_DATA_ + (uint32_t)(i*4U + 2U));
						 _Array_DATA_[i]  =     (uint32_t)((left&0x0000FFFF)|(right<<16U));
						 */
		_Array_DATA_[i]  = (*(uint16_t*)(_ADDRESS_DATA_ + (uint32_t)(i*4U))) | ((*(uint16_t*)(_ADDRESS_DATA_ + (uint32_t)(i*4U + 2U)))<<16U);
						 
	          
					 }
	  
	//___enable_systick___
	    uwTickFreq = tick_freq;
     	uwTick     = tick_var;
    	SET_BIT(SysTick->CTRL, SysTick_CTRL_TICKINT_Msk);	 
}

//______________________________________________________________________________________________________


//______________________________________________________________________________________________________


uint32_t Int_to_Uint_Convert(int32_t _DATA_)
{
	return  (uint32_t)_DATA_;
}

void Int_to_Uint_16bit_Array_Convert(int16_t* _DATA_S, uint16_t* _DATA_D_, uint16_t _LENGTH_)
{
	for(uint16_t i = 0; i <_LENGTH_;i++)
	{
		_DATA_D_[i]  = (uint16_t)_DATA_S[i];
	}
}

void Int_to_Uint_32bit_Array_Convert(int32_t* _DATA_S, uint32_t* _DATA_D_, uint16_t _LENGTH_)
{
	for(uint16_t i = 0; i <_LENGTH_;i++)
	{
		_DATA_D_[i]  = (uint32_t)_DATA_S[i];
	}
}

int32_t Uint_to_Int_Convert(int32_t _DATA_)
{
	return  (int32_t)_DATA_;
}

void Uint_to_Int_16bit_Array_Convert(uint16_t* _DATA_S, int16_t* _DATA_D_, uint16_t _LENGTH_)
{
	for(uint16_t i = 0; i <_LENGTH_;i++)
	{
		_DATA_D_[i]  = (int16_t)_DATA_S[i];
	}
}

void Uint_to_Int_32bit_Array_Convert(uint32_t* _DATA_S, int32_t* _DATA_D_, uint16_t _LENGTH_)
{
	for(uint16_t i = 0; i <_LENGTH_;i++)
	{
		_DATA_D_[i]  = (int32_t)_DATA_S[i];
	}
}




/*
    //_________STATIC FUNTION__________NO USE____

*/

static void deleteBuffer8(uint8_t* data, uint16_t _LENGTH_)
{
	for(uint16_t i = 0; i < _LENGTH_; i++)
	{
		data[i] = 0;
	}
}

static void deleteBuffer16(uint16_t* data, uint16_t _LENGTH_)
{
	for(uint16_t i = 0; i < _LENGTH_; i++)
	{
		data[i] = 0;
	}
}
static void deleteBuffer32(uint32_t* data, uint16_t _LENGTH_)
{
	for(uint16_t i = 0; i < _LENGTH_; i++)
	{
		data[i] = 0;
	}
}
//___ Clear_page_________
static void Flash_Erase(uint32_t addr) 
{ 
  while((FLASH->SR&FLASH_SR_BSY));
  FLASH->CR |= FLASH_CR_PER;    //Page Erase Set
  FLASH->AR = addr;              //Page Address
  FLASH->CR |= FLASH_CR_STRT; //Start Page Erase
  while((FLASH->SR&FLASH_SR_BSY));
	FLASH->CR &= ~FLASH_SR_BSY;
  FLASH->CR &= ~FLASH_CR_PER; //Page Erase Clear
}

//______________________________________________________________________________________

