/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * Copyright (c) 2024 STMicroelectronics.
  * All rights reserved.
  *
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  *
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "cmsis_os.h"

/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include <stdlib.h>
#include "LoRa.h"
#include <string.h>
#include "cJSON.h"
#include "i2c-lcd.h"
#include "flash.h"
/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
cJSON* jsonTemp;
cJSON *id_item;

//=========================================================MY ID DEFINE===========================================================
#define myID "ND7969"
typedef struct id{
  int numberID;
  uint32_t addrStart;
	uint32_t addrEnd;
  uint32_t addrNumberID;
  char ids[100][6];
}id;

id managerID;
//=========================================================Address Flash DEFINE===================================================
#define START_ADDRESS 		((uint32_t)0x0801F810)	//Page 126
#define NUMBER_ID_ADDRESS	((uint32_t)0x0801F402)	//Page 126

//========================================================ALL SENSOR DEFINE=======================================================
//#define SHT3x
//#define SAL //Sensor Sal
//#define KIT //Kit Weather Station
#define FLOW_WATER //sensor flow water 
//#define ULTRASONIC //level water
//#define LCD

//========================================================DEFINE EVENT===========================================================
#define EVENT0 (1UL << 0)
#define EVENT1 (1UL << 1)
#define EVENT2 (1UL << 2)
#define EVENT3 (1UL << 3)

/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
ADC_HandleTypeDef hadc1;
ADC_HandleTypeDef hadc2;

I2C_HandleTypeDef hi2c1;
I2C_HandleTypeDef hi2c2;

SPI_HandleTypeDef hspi1;

TIM_HandleTypeDef htim2;

UART_HandleTypeDef huart1;
UART_HandleTypeDef huart2;

osThreadId sendMessageHandle;
osThreadId recvMessageHandle;
osSemaphoreId myBinarySemSendHandle;
osSemaphoreId myBinarySemRecvHandle;
/* USER CODE BEGIN PV */

LoRa myLoRa;
char readData[128];
char sendData[128];
int	rssi;
char msg[64];
char messRecv[200];
 
int i = 0;
//=======================================================INIT json data==============================================================
char jsonPack[250];
//=======================================================INIT Sensors==========================================================
typedef struct{
	#ifdef SHT3x
	int valTemp;
	char valTempToStr[10];
	int valHum;
	char valHumToStr[10];
	#endif
	
	#ifdef SAL
	float valEC;
	char valECToStr[10];
	#endif
	
	#ifdef KIT
	int valWindSpeed;
	int valWindDirection;
	int valTempKit;
	int valHumKit;
	
	char valSpeedToStr[10];
	char valDirectionToStr[10];
	char valTempKitToStr[10];
	char valHumKitToStr[10];
	#endif
	
	#ifdef FLOW_WATER
	int valQ;
	char valQToStr[10];
	#endif
	
	#ifdef ULTRASONIC
	int valLevelWater;
	char valLevelWaterToStr[10];
	#endif
}sensors;

sensors sensorVals;

//====================================================================INIT TIME===================================================================
int lastTime = 0;
int wait = 5000;

/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_SPI1_Init(void);
static void MX_USART1_UART_Init(void);
static void MX_ADC1_Init(void);
static void MX_ADC2_Init(void);
static void MX_I2C1_Init(void);
static void MX_I2C2_Init(void);
static void MX_USART2_UART_Init(void);
static void MX_TIM2_Init(void);
void sendMessageTask(void const * argument);
void recvMessageTask(void const * argument);

/* USER CODE BEGIN PFP */
//======================================================Function for Json====================================================
void formatJson();
void proceedJson(char *DataJSON);
void mergeJson(cJSON *json1, cJSON *json2);

//======================================================Function for Flash===================================================
void writeIDToFlash(char *id);
void readIDFromFlash();
int loo = 0;

//======================================================Funtion PRINT================================================================
void printUart(char *str);

/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */
void HAL_GPIO_EXTI_Callback(uint16_t GPIO_Pin);

osMutexDef(myMutex01);
/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void)
{

  /* USER CODE BEGIN 1 */
	//==================================================================INIT FLASH====================================================================

	managerID.addrStart = START_ADDRESS;
	managerID.addrEnd = START_ADDRESS;
	managerID.addrNumberID = NUMBER_ID_ADDRESS;
	managerID.numberID = 0;
	
	writeIDToFlash("ND7961");
//	readIDFromFlash();
  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_SPI1_Init();
  MX_USART1_UART_Init();
  MX_ADC1_Init();
  MX_ADC2_Init();
  MX_I2C1_Init();
  MX_I2C2_Init();
  MX_USART2_UART_Init();
  MX_TIM2_Init();
  /* USER CODE BEGIN 2 */
	myLoRa = newLoRa();
	
	myLoRa.hSPIx                 = &hspi1;
	myLoRa.CS_port               = NSS_GPIO_Port;
	myLoRa.CS_pin                = NSS_Pin;
	myLoRa.reset_port            = RST_GPIO_Port;
	myLoRa.reset_pin             = RST_Pin;
	myLoRa.DIO0_port						 = DIO0_GPIO_Port;
	myLoRa.DIO0_pin							 = DIO0_Pin;
	
	myLoRa.frequency             = 433;							  // default = 433 MHz
	myLoRa.spredingFactor        = SF_7;							// default = SF_7
	myLoRa.bandWidth			       = BW_125KHz;				  // default = BW_125KHz
	myLoRa.crcRate				       = CR_4_5;						// default = CR_4_5
	myLoRa.power					       = POWER_20db;				// default = 20db
	myLoRa.overCurrentProtection = 120; 							// default = 100 mA
	myLoRa.preamble				       = 10;		  					// default = 8;
	
	LoRa_reset(&myLoRa);
	uint16_t LoRa_status = LoRa_init(&myLoRa);
	
	// START CONTINUOUS RECEIVING -----------------------------------
	LoRa_startReceiving(&myLoRa);
	if (LoRa_status==LORA_OK){
		LoRa_transmit(&myLoRa, (uint8_t*)sendData, 120, 100);
		printUart("LoRa is running...");
	}
	else{
		printUart("\n\r LoRa failed :( \n\r Error code: %d");
	}
	
	//======================================================================INIT LCD======================================================================
	#ifdef LCD
	lcd_init();
	#endif
	
  /* USER CODE END 2 */

  /* USER CODE BEGIN RTOS_MUTEX */
  /* add mutexes, ... */
  /* USER CODE END RTOS_MUTEX */

  /* Create the semaphores(s) */
  /* definition and creation of myBinarySemSend */
  osSemaphoreDef(myBinarySemSend);
  myBinarySemSendHandle = osSemaphoreCreate(osSemaphore(myBinarySemSend), 1);

  /* definition and creation of myBinarySemRecv */
  osSemaphoreDef(myBinarySemRecv);
  myBinarySemRecvHandle = osSemaphoreCreate(osSemaphore(myBinarySemRecv), 1);

  /* USER CODE BEGIN RTOS_SEMAPHORES */
  /* add semaphores, ... */
  /* USER CODE END RTOS_SEMAPHORES */

  /* USER CODE BEGIN RTOS_TIMERS */
  /* start timers, add new ones, ... */
  /* USER CODE END RTOS_TIMERS */

  /* USER CODE BEGIN RTOS_QUEUES */
  /* add queues, ... */
  /* USER CODE END RTOS_QUEUES */

  /* Create the thread(s) */
  /* definition and creation of sendMessage */
  osThreadDef(sendMessage, sendMessageTask, osPriorityHigh, 0, 128);
  sendMessageHandle = osThreadCreate(osThread(sendMessage), NULL);

//  /* definition and creation of recvMessage */
  osThreadDef(recvMessage, recvMessageTask, osPriorityAboveNormal, 0, 128);
  recvMessageHandle = osThreadCreate(osThread(recvMessage), NULL);

  /* USER CODE BEGIN RTOS_THREADS */
  /* add threads, ... */
  /* USER CODE END RTOS_THREADS */

  /* Start scheduler */
  osKernelStart();

  /* We should never get here as control is now taken by the scheduler */

  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
  }
  /* USER CODE END 3 */
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};
  RCC_PeriphCLKInitTypeDef PeriphClkInit = {0};

  /** Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSE;
  RCC_OscInitStruct.HSEState = RCC_HSE_ON;
  RCC_OscInitStruct.HSEPredivValue = RCC_HSE_PREDIV_DIV1;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLMUL = RCC_PLL_MUL9;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }

  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV2;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_2) != HAL_OK)
  {
    Error_Handler();
  }
  PeriphClkInit.PeriphClockSelection = RCC_PERIPHCLK_ADC;
  PeriphClkInit.AdcClockSelection = RCC_ADCPCLK2_DIV8;
  if (HAL_RCCEx_PeriphCLKConfig(&PeriphClkInit) != HAL_OK)
  {
    Error_Handler();
  }
}

/**
  * @brief ADC1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_ADC1_Init(void)
{

  /* USER CODE BEGIN ADC1_Init 0 */

  /* USER CODE END ADC1_Init 0 */

  ADC_ChannelConfTypeDef sConfig = {0};

  /* USER CODE BEGIN ADC1_Init 1 */

  /* USER CODE END ADC1_Init 1 */

  /** Common config
  */
  hadc1.Instance = ADC1;
  hadc1.Init.ScanConvMode = ADC_SCAN_DISABLE;
  hadc1.Init.ContinuousConvMode = DISABLE;
  hadc1.Init.DiscontinuousConvMode = DISABLE;
  hadc1.Init.ExternalTrigConv = ADC_SOFTWARE_START;
  hadc1.Init.DataAlign = ADC_DATAALIGN_RIGHT;
  hadc1.Init.NbrOfConversion = 1;
  if (HAL_ADC_Init(&hadc1) != HAL_OK)
  {
    Error_Handler();
  }

  /** Configure Regular Channel
  */
  sConfig.Channel = ADC_CHANNEL_0;
  sConfig.Rank = ADC_REGULAR_RANK_1;
  sConfig.SamplingTime = ADC_SAMPLETIME_1CYCLE_5;
  if (HAL_ADC_ConfigChannel(&hadc1, &sConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN ADC1_Init 2 */

  /* USER CODE END ADC1_Init 2 */

}

/**
  * @brief ADC2 Initialization Function
  * @param None
  * @retval None
  */
static void MX_ADC2_Init(void)
{

  /* USER CODE BEGIN ADC2_Init 0 */

  /* USER CODE END ADC2_Init 0 */

  ADC_ChannelConfTypeDef sConfig = {0};

  /* USER CODE BEGIN ADC2_Init 1 */

  /* USER CODE END ADC2_Init 1 */

  /** Common config
  */
  hadc2.Instance = ADC2;
  hadc2.Init.ScanConvMode = ADC_SCAN_DISABLE;
  hadc2.Init.ContinuousConvMode = DISABLE;
  hadc2.Init.DiscontinuousConvMode = DISABLE;
  hadc2.Init.ExternalTrigConv = ADC_SOFTWARE_START;
  hadc2.Init.DataAlign = ADC_DATAALIGN_RIGHT;
  hadc2.Init.NbrOfConversion = 1;
  if (HAL_ADC_Init(&hadc2) != HAL_OK)
  {
    Error_Handler();
  }

  /** Configure Regular Channel
  */
  sConfig.Channel = ADC_CHANNEL_1;
  sConfig.Rank = ADC_REGULAR_RANK_1;
  sConfig.SamplingTime = ADC_SAMPLETIME_1CYCLE_5;
  if (HAL_ADC_ConfigChannel(&hadc2, &sConfig) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN ADC2_Init 2 */

  /* USER CODE END ADC2_Init 2 */

}

/**
  * @brief I2C1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_I2C1_Init(void)
{

  /* USER CODE BEGIN I2C1_Init 0 */

  /* USER CODE END I2C1_Init 0 */

  /* USER CODE BEGIN I2C1_Init 1 */

  /* USER CODE END I2C1_Init 1 */
  hi2c1.Instance = I2C1;
  hi2c1.Init.ClockSpeed = 100000;
  hi2c1.Init.DutyCycle = I2C_DUTYCYCLE_2;
  hi2c1.Init.OwnAddress1 = 0;
  hi2c1.Init.AddressingMode = I2C_ADDRESSINGMODE_7BIT;
  hi2c1.Init.DualAddressMode = I2C_DUALADDRESS_DISABLE;
  hi2c1.Init.OwnAddress2 = 0;
  hi2c1.Init.GeneralCallMode = I2C_GENERALCALL_DISABLE;
  hi2c1.Init.NoStretchMode = I2C_NOSTRETCH_DISABLE;
  if (HAL_I2C_Init(&hi2c1) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN I2C1_Init 2 */

  /* USER CODE END I2C1_Init 2 */

}

/**
  * @brief I2C2 Initialization Function
  * @param None
  * @retval None
  */
static void MX_I2C2_Init(void)
{

  /* USER CODE BEGIN I2C2_Init 0 */

  /* USER CODE END I2C2_Init 0 */

  /* USER CODE BEGIN I2C2_Init 1 */

  /* USER CODE END I2C2_Init 1 */
  hi2c2.Instance = I2C2;
  hi2c2.Init.ClockSpeed = 100000;
  hi2c2.Init.DutyCycle = I2C_DUTYCYCLE_2;
  hi2c2.Init.OwnAddress1 = 0;
  hi2c2.Init.AddressingMode = I2C_ADDRESSINGMODE_7BIT;
  hi2c2.Init.DualAddressMode = I2C_DUALADDRESS_DISABLE;
  hi2c2.Init.OwnAddress2 = 0;
  hi2c2.Init.GeneralCallMode = I2C_GENERALCALL_DISABLE;
  hi2c2.Init.NoStretchMode = I2C_NOSTRETCH_DISABLE;
  if (HAL_I2C_Init(&hi2c2) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN I2C2_Init 2 */

  /* USER CODE END I2C2_Init 2 */

}

/**
  * @brief SPI1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_SPI1_Init(void)
{

  /* USER CODE BEGIN SPI1_Init 0 */

  /* USER CODE END SPI1_Init 0 */

  /* USER CODE BEGIN SPI1_Init 1 */

  /* USER CODE END SPI1_Init 1 */
  /* SPI1 parameter configuration*/
  hspi1.Instance = SPI1;
  hspi1.Init.Mode = SPI_MODE_MASTER;
  hspi1.Init.Direction = SPI_DIRECTION_2LINES;
  hspi1.Init.DataSize = SPI_DATASIZE_8BIT;
  hspi1.Init.CLKPolarity = SPI_POLARITY_LOW;
  hspi1.Init.CLKPhase = SPI_PHASE_1EDGE;
  hspi1.Init.NSS = SPI_NSS_SOFT;
  hspi1.Init.BaudRatePrescaler = SPI_BAUDRATEPRESCALER_8;
  hspi1.Init.FirstBit = SPI_FIRSTBIT_MSB;
  hspi1.Init.TIMode = SPI_TIMODE_DISABLE;
  hspi1.Init.CRCCalculation = SPI_CRCCALCULATION_DISABLE;
  hspi1.Init.CRCPolynomial = 10;
  if (HAL_SPI_Init(&hspi1) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN SPI1_Init 2 */

  /* USER CODE END SPI1_Init 2 */

}

/**
  * @brief TIM2 Initialization Function
  * @param None
  * @retval None
  */
static void MX_TIM2_Init(void)
{

  /* USER CODE BEGIN TIM2_Init 0 */

  /* USER CODE END TIM2_Init 0 */

  TIM_MasterConfigTypeDef sMasterConfig = {0};
  TIM_IC_InitTypeDef sConfigIC = {0};

  /* USER CODE BEGIN TIM2_Init 1 */

  /* USER CODE END TIM2_Init 1 */
  htim2.Instance = TIM2;
  htim2.Init.Prescaler = 0;
  htim2.Init.CounterMode = TIM_COUNTERMODE_UP;
  htim2.Init.Period = 65535;
  htim2.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1;
  htim2.Init.AutoReloadPreload = TIM_AUTORELOAD_PRELOAD_DISABLE;
  if (HAL_TIM_IC_Init(&htim2) != HAL_OK)
  {
    Error_Handler();
  }
  sMasterConfig.MasterOutputTrigger = TIM_TRGO_RESET;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  if (HAL_TIMEx_MasterConfigSynchronization(&htim2, &sMasterConfig) != HAL_OK)
  {
    Error_Handler();
  }
  sConfigIC.ICPolarity = TIM_INPUTCHANNELPOLARITY_RISING;
  sConfigIC.ICSelection = TIM_ICSELECTION_DIRECTTI;
  sConfigIC.ICPrescaler = TIM_ICPSC_DIV1;
  sConfigIC.ICFilter = 0;
  if (HAL_TIM_IC_ConfigChannel(&htim2, &sConfigIC, TIM_CHANNEL_2) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN TIM2_Init 2 */

  /* USER CODE END TIM2_Init 2 */

}

/**
  * @brief USART1 Initialization Function
  * @param None
  * @retval None
  */
static void MX_USART1_UART_Init(void)
{

  /* USER CODE BEGIN USART1_Init 0 */

  /* USER CODE END USART1_Init 0 */

  /* USER CODE BEGIN USART1_Init 1 */

  /* USER CODE END USART1_Init 1 */
  huart1.Instance = USART1;
  huart1.Init.BaudRate = 115200;
  huart1.Init.WordLength = UART_WORDLENGTH_8B;
  huart1.Init.StopBits = UART_STOPBITS_1;
  huart1.Init.Parity = UART_PARITY_NONE;
  huart1.Init.Mode = UART_MODE_TX_RX;
  huart1.Init.HwFlowCtl = UART_HWCONTROL_NONE;
  huart1.Init.OverSampling = UART_OVERSAMPLING_16;
  if (HAL_UART_Init(&huart1) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN USART1_Init 2 */

  /* USER CODE END USART1_Init 2 */

}

/**
  * @brief USART2 Initialization Function
  * @param None
  * @retval None
  */
static void MX_USART2_UART_Init(void)
{

  /* USER CODE BEGIN USART2_Init 0 */

  /* USER CODE END USART2_Init 0 */

  /* USER CODE BEGIN USART2_Init 1 */

  /* USER CODE END USART2_Init 1 */
  huart2.Instance = USART2;
  huart2.Init.BaudRate = 115200;
  huart2.Init.WordLength = UART_WORDLENGTH_8B;
  huart2.Init.StopBits = UART_STOPBITS_1;
  huart2.Init.Parity = UART_PARITY_NONE;
  huart2.Init.Mode = UART_MODE_TX_RX;
  huart2.Init.HwFlowCtl = UART_HWCONTROL_NONE;
  huart2.Init.OverSampling = UART_OVERSAMPLING_16;
  if (HAL_UART_Init(&huart2) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN USART2_Init 2 */

  /* USER CODE END USART2_Init 2 */

}

/**
  * @brief GPIO Initialization Function
  * @param None
  * @retval None
  */
static void MX_GPIO_Init(void)
{
  GPIO_InitTypeDef GPIO_InitStruct = {0};
/* USER CODE BEGIN MX_GPIO_Init_1 */
/* USER CODE END MX_GPIO_Init_1 */

  /* GPIO Ports Clock Enable */
  __HAL_RCC_GPIOD_CLK_ENABLE();
  __HAL_RCC_GPIOA_CLK_ENABLE();
  __HAL_RCC_GPIOB_CLK_ENABLE();

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(NSS_GPIO_Port, NSS_Pin, GPIO_PIN_SET);

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(RST_GPIO_Port, RST_Pin, GPIO_PIN_SET);

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOB, GPIO_PIN_13|GPIO_PIN_14|GPIO_PIN_15, GPIO_PIN_RESET);

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(GPIOA, GPIO_PIN_8, GPIO_PIN_RESET);

  /*Configure GPIO pins : NSS_Pin PA8 */
  GPIO_InitStruct.Pin = NSS_Pin|GPIO_PIN_8;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
  HAL_GPIO_Init(GPIOA, &GPIO_InitStruct);

  /*Configure GPIO pins : RST_Pin PB13 PB14 PB15 */
  GPIO_InitStruct.Pin = RST_Pin|GPIO_PIN_13|GPIO_PIN_14|GPIO_PIN_15;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
  HAL_GPIO_Init(GPIOB, &GPIO_InitStruct);

  /*Configure GPIO pin : DIO0_Pin */
  GPIO_InitStruct.Pin = DIO0_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_IT_RISING;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  HAL_GPIO_Init(DIO0_GPIO_Port, &GPIO_InitStruct);

  /* EXTI interrupt init*/
  HAL_NVIC_SetPriority(EXTI9_5_IRQn, 14, 0);
  HAL_NVIC_EnableIRQ(EXTI9_5_IRQn);

/* USER CODE BEGIN MX_GPIO_Init_2 */
/* USER CODE END MX_GPIO_Init_2 */
}

/* USER CODE BEGIN 4 */
void formatJson(){
    // Xóa m?ng d? li?u
	loo++;
    #ifdef SHT3x
    sensorVals.valTemp = loo;
    sensorVals.valHum = 0;
    #endif
    
    #ifdef SAL
    sensorVals.valEC = 0;
    #endif
    
    #ifdef KIT
    sensorVals.valHumKit = 0;
    sensorVals.valTempKit = 0;
    sensorVals.valWindDirection= 0;
    sensorVals.valWindSpeed = 0;
    #endif
    
    #ifdef ULTRASONIC
    sensorVals.valLevelWater = 0;
    #endif
    
    #ifdef FLOW_WATER
    sensorVals.valQ = 0;
    #endif

    memset(jsonPack, 0, sizeof(jsonPack));

    #ifdef SHT3x
    memset(sensorVals.valTempToStr, 0, sizeof(sensorVals.valTempToStr));
    memset(sensorVals.valHumToStr, 0, sizeof(sensorVals.valHumToStr));
    #endif
    
    #ifdef KIT
    memset(sensorVals.valSpeedToStr, 0, sizeof(sensorVals.valSpeedToStr));
    memset(sensorVals.valDirectionToStr, 0, sizeof(sensorVals.valDirectionToStr));
    memset(sensorVals.valTempKitToStr, 0, sizeof(sensorVals.valTempKitToStr));
    memset(sensorVals.valHumKitToStr, 0, sizeof(sensorVals.valHumKitToStr));
    #endif
    
    #ifdef FLOW_WATER
    memset(sensorVals.valQToStr, 0, sizeof(sensorVals.valQToStr));
    #endif
    
    #ifdef ULTRASONIC
    memset(sensorVals.valLevelWaterToStr, 0, sizeof(sensorVals.valLevelWaterToStr));
    #endif

    
    #ifdef SHT3x
    sprintf(sensorVals.valTempToStr, "%d", sensorVals.valTemp);
    sprintf(sensorVals.valHumToStr, "%d", sensorVals.valHum);
    #endif
    
    #ifdef KIT
    sprintf(sensorVals.valSpeedToStr, "%d", sensorVals.valWindSpeed);
    sprintf(sensorVals.valDirectionToStr, "%d", sensorVals.valWindDirection);
    sprintf(sensorVals.valTempKitToStr, "%d", sensorVals.valTempKit);
    sprintf(sensorVals.valHumKitToStr, "%d", sensorVals.valHumKit);
    #endif
    
    #ifdef FLOW_WATER
    sprintf(sensorVals.valQToStr, "%d", sensorVals.valQ);
    #endif
    
    #ifdef ULTRASONIC
    sprintf(sensorVals.valLevelWaterToStr, "%d", sensorVals.valLevelWater);
    #endif

    // T?o chu?i JSON
    strcat(jsonPack, "{\"ID\":\"");
    strcat(jsonPack, myID);
    strcat(jsonPack, "\"");

    #ifdef SHT3x
    strcat(jsonPack, ",\"TEMP\":\"");
    strcat(jsonPack, sensorVals.valTempToStr);
    strcat(jsonPack, "\",\"HUM\":\"");
    strcat(jsonPack, sensorVals.valHumToStr);
    strcat(jsonPack, "\"");
    #endif
    
    #ifdef KIT
    strcat(jsonPack, ",\"WP\":\"");
    strcat(jsonPack, sensorVals.valSpeedToStr);
    strcat(jsonPack, "\",\"WD\":\"");
    strcat(jsonPack, sensorVals.valDirectionToStr);
    strcat(jsonPack, "\",\"TempKit\":\"");
    strcat(jsonPack, sensorVals.valTempKitToStr);
    strcat(jsonPack, "\",\"HumKit\":\"");
    strcat(jsonPack, sensorVals.valHumKitToStr);
    strcat(jsonPack, "\"");
    #endif
    
    #ifdef FLOW_WATER
    strcat(jsonPack, ",\"Q\":\"");
    strcat(jsonPack, sensorVals.valQToStr);
    strcat(jsonPack, "\"");
    #endif
    
    #ifdef ULTRASONIC
    strcat(jsonPack, ",\"LevelWater\":\"");
    strcat(jsonPack, sensorVals.valLevelWaterToStr);
    strcat(jsonPack, "\"");
    #endif

    strcat(jsonPack, "}");
    int i = 0;
}


void mergeJsonStrings(char* json2) {
		size_t len = strlen(jsonPack);
    if (len > 0) {
        jsonPack[len - 1] = '\0'; // Ghi dè ký t? cu?i b?ng ký t? k?t thúc chu?i '\0'
    }
		
    // Thêm d?u ph?y d? ngan cách các ph?n t?
    strcat(jsonPack, ",");

    // Duy?t qua các ph?n t? c?a chu?i JSON th? hai (lo?i b? d?u ngo?c m?)
    char* start = json2 + 1;
    while (*start != '\0') {
        // B? qua kho?ng tr?ng
        while (*start == ' ' || *start == '\n' || *start == '\r' || *start == '\t') {
            start++;
        }

        // Ki?m tra n?u ph?n t? là "ID"
        if (strncmp(start, "\"ID\"", 4) == 0) {
            // B? qua ph?n t? "ID"
            start = strchr(start, ',');
            if (start == NULL) {
                break;
            }
            start++; // B? qua d?u ph?y
            continue;
        }

        // Sao chép ph?n t? t? json2 vào result
        char* end = strchr(start, ',');
        if (end == NULL) {
            end = strchr(start, '}');
        }
        if (end == NULL) {
            break;
        }

        int length = end - start;
        if (strlen(jsonPack) + length < 250 - 1) {
            strncat(jsonPack, start, length);
            strcat(jsonPack, ",");
        } else {
//            printf("Result buffer too small.\n");
            return;
        }

        start = end + 1;
    }

    // Thêm ph?n còn l?i c?a json2 (sau d?u ph?y cu?i cùng)
    char* lastPart = strchr(start, '{');
    if (lastPart != NULL) {
        start = lastPart + 1; // B? qua d?u ngo?c m?
    }

    if (*start != '\0' && strchr(start, '}') != NULL) {
        if (strlen(jsonPack) + strlen(start) - 1 < 250 - 1) {
            strncat(jsonPack, start, strlen(start) - 1); // Sao chép ph?n còn l?i tr? d?u ngo?c dóng
        } else {
//            printf("Result buffer too small.\n");
            return;
        }
    }

    // Thay d?u ph?y cu?i cùng b?ng d?u ngo?c dóng
    if (strlen(jsonPack) > 0 && jsonPack[strlen(jsonPack) - 1] == ',') {
        jsonPack[strlen(jsonPack) - 1] = '}';
    } else {
        strcat(jsonPack, "}");
    }
}

void proceedJson(char *DataJSON){
	cJSON *strJson = cJSON_Parse(DataJSON);
	if(!strJson)
	{
		printUart("JSON ERROR!");
	}
	else
	{
		printUart("JSON OK!");
	}
}


void writeIDToFlash(char *id){
	strcpy(managerID.ids[managerID.numberID], id);
	Flash_Write_String((uint8_t *)id, managerID.addrEnd, 6);
	managerID.addrEnd|=(strlen(id)*2);
	managerID.numberID++;
	
	Flash_Write_Uint((uint16_t)managerID.numberID, managerID.addrNumberID);
}

void readIDFromFlash(){
	uint16_t numberIDTemp;
	numberIDTemp = Flash_Read_Uint(managerID.addrNumberID);
	managerID.numberID = numberIDTemp;
	for(int i = 0; i < numberIDTemp; i++){
		uint8_t idTemp[6];
		Flash_Read_String(idTemp, managerID.addrStart|((0x0C) * i), 6);
		strcpy(managerID.ids[i], (char *)idTemp);
	}
}

void printUart(char *str){
	char messPrint[200];
	sprintf(messPrint,"%s\r\n",str);
	HAL_UART_Transmit(&huart1,(uint8_t *)messPrint,strlen(messPrint),1000);
}


void HAL_GPIO_EXTI_Callback(uint16_t GPIO_Pin){
	if(GPIO_Pin == myLoRa.DIO0_pin){
		LoRa_receive(&myLoRa, (uint8_t*)messRecv, 200);
		rssi = LoRa_getRSSI(&myLoRa);
		if(strcmp(messRecv,myID)==0){
			osSemaphoreRelease(myBinarySemSendHandle);
		}else{
//			printUart(messRecv);
//			sprintf(msg,"%d\r\n",strlen(messRecv));
//			HAL_UART_Transmit(&huart1,(uint8_t *)msg,strlen(msg),1000);
			jsonTemp = cJSON_Parse(messRecv);
			if(!jsonTemp)
			{
				printUart("JSON ERROR!");
			}
			else
			{
				printUart("JSON OK!");
			}
		}
	}
}



/* USER CODE END 4 */

/* USER CODE BEGIN Header_sendMessageTask */
/**
  * @brief  Function implementing the sendMessage thread.
  * @param  argument: Not used
  * @retval None
  */
/* USER CODE END Header_sendMessageTask */
void sendMessageTask(void const * argument)
{
  /* USER CODE BEGIN 5 */
	sprintf(msg,"%d",i);
	lcd_goto_XY(1,0);
	lcd_send_string(msg);
	formatJson();
  /* Infinite loop */
  for(;;)
  {
		printUart("SEND TASK");
//		osSemaphoreWait(myBinarySemSendHandle, osWaitForever);
		formatJson();
//		if(strcmp(messRecv,myID)==0){
			sprintf(msg,"%d",i);
			lcd_goto_XY(1,0);
			lcd_send_string(msg);

			printUart("Reading sht30");
			HAL_Delay(100);
			printUart("Reading SEN2");
			HAL_Delay(100);		
			printUart("Reading SEN3");
			HAL_Delay(100);
			printUart("Reading SEN4");
			HAL_Delay(100);
			printUart("Reading SEN5");
			HAL_Delay(100);
		
			if(managerID.numberID != 0){
				for(int element = 0; element < managerID.numberID; element++){
					int st = 0;
					while(st < 5){
						if(HAL_GetTick() - lastTime > wait){
							LoRa_transmit(&myLoRa, (uint8_t*)managerID.ids[element], strlen(managerID.ids[element]), 100);
							st++;
							printUart(managerID.ids[element]);
							lastTime = HAL_GetTick();
						}
						cJSON *id_item = cJSON_GetObjectItemCaseSensitive(jsonTemp, "ID");
						if (cJSON_IsString(id_item) && (id_item->valuestring != NULL)) {
								if (strcmp(id_item->valuestring, managerID.ids[element]) == 0) {
									size_t len = strlen(jsonPack);
									char* tempDataSensor = jsonPack;
									mergeJsonStrings(messRecv);
									cJSON_Delete(id_item);
									break;
								}
						}
					}
					st = 0;
				}
			}
		
			LoRa_transmit(&myLoRa, (uint8_t*)jsonPack, strlen(jsonPack), 1000);
			HAL_UART_Transmit(&huart1,(uint8_t *)jsonPack,strlen(jsonPack),1000);
			sprintf(messRecv,"%s","");
			i++;
		}
//	}
  /* USER CODE END 5 */
}

/* USER CODE BEGIN Header_recvMessageTask */
/**
* @brief Function implementing the recvMessage thread.
* @param argument: Not used
* @retval None
*/
/* USER CODE END Header_recvMessageTask */
void recvMessageTask(void const * argument)
{
  /* USER CODE BEGIN recvMessageTask */
	int i = 0;
//  /* Infinite loop */
  for(;;)
  {
		printUart("RECV TASK");
		osSemaphoreWait(myBinarySemRecvHandle, osWaitForever);
		printUart("RECV TASK OK");
		
		char *id = "ND7969";
		if(HAL_GetTick() - lastTime > wait){
			LoRa_transmit(&myLoRa, (uint8_t*)id, strlen(id), 100);
			lastTime = HAL_GetTick();
		}
		sprintf(msg, "%s", messRecv);
		lcd_goto_XY(1,0);
		lcd_send_string(msg);
		sprintf(msg,"RSSI: %d\r\n",rssi);
		lcd_goto_XY(2,0);
		lcd_send_string(msg);
	}
  /* USER CODE END recvMessageTask */
}

/**
  * @brief  Period elapsed callback in non blocking mode
  * @note   This function is called  when TIM4 interrupt took place, inside
  * HAL_TIM_IRQHandler(). It makes a direct call to HAL_IncTick() to increment
  * a global variable "uwTick" used as application time base.
  * @param  htim : TIM handle
  * @retval None
  */
void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim)
{
  /* USER CODE BEGIN Callback 0 */

  /* USER CODE END Callback 0 */
  if (htim->Instance == TIM4) {
    HAL_IncTick();
  }
  /* USER CODE BEGIN Callback 1 */

  /* USER CODE END Callback 1 */
}

/**
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */
  __disable_irq();
  while (1)
  {
  }
  /* USER CODE END Error_Handler_Debug */
}

#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */
