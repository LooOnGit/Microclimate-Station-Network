/*
    [*] WRITE BY BUI VAN HUY
		[*] 28/11/2019

*/

#include "stm32f1xx_hal.h"

// define page address
#define _PAGE_127_ 		 	((uint32_t)0x0801FC02)
#define _PAGE_126_ 		 	((uint32_t)0x0801F802)
#define _PAGE_125_ 		 	((uint32_t)0x0801F402)
#define _PAGE_124_ 		 	((uint32_t)0x0801F002)
#define _PAGE_123_ 		 	((uint32_t)0x0801EC02)
#define _PAGE_122_ 		 	((uint32_t)0x0801E802)
#define _PAGE_121_ 		 	((uint32_t)0x0801E402)
#define _PAGE_120_ 		 	((uint32_t)0x0801E002)
#define _PAGE_119_ 		 	((uint32_t)0x0801DC02)
#define _PAGE_118_ 		 	((uint32_t)0x0801D802)

extern __IO uint32_t uwTick;

//__WRITE__
void       Flash_Write_Uint(uint16_t _DATA_, uint32_t _ADDRESS_DATA_);
void       Flash_Write_Array_16bit(uint16_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_);
void       Flash_Write_Array_32bit(uint32_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_);
void       Flash_Write_String(uint8_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_);

//__READ__
uint32_t   Flash_Read_Uint(uint32_t _ADDRESS_DATA_);
void       Flash_Read_Array_16bit(uint16_t*  _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_); 
void       Flash_Read_Array_32bit(uint32_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_);
void       Flash_Read_String(uint8_t* _Array_DATA_, uint32_t _ADDRESS_DATA_, uint16_t _LENGTH_);

//__CONVERT__
uint32_t   Int_to_Uint_Convert(int32_t _DATA_);
void       Int_to_Uint_16bit_Array_Convert(int16_t* _DATA_S, uint16_t* _DATA_D_, uint16_t _LENGTH_);
void       Int_to_Uint_32bit_Array_Convert(int32_t* _DATA_S, uint32_t* _DATA_D_, uint16_t _LENGTH_);
int32_t    Uint_to_Int_Convert(int32_t _DATA_);
void       Uint_to_Int_16bit_Array_Convert(uint16_t* _DATA_S, int16_t* _DATA_D_, uint16_t _LENGTH_);
void       Uint_to_Int_32bit_Array_Convert(uint32_t* _DATA_S, int32_t* _DATA_D_, uint16_t _LENGTH_);

