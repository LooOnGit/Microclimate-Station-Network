#include <stdio.h>
#include <string.h>
#include "cJSON.h"

#define JSON_SIZE 250

// Giả sử cấu trúc dữ liệu của bạn như sau:
typedef struct {
    int valTemp;
    int valHum;
    int valEC;
    int valWindDirection;
    int valWindSpeed;
    int valTempKit;
    int valHumKit;
    int valLevelWater;
    int valQ;
    char valTempToStr[10];
    char valHumToStr[10];
    char valSpeedToStr[10];
    char valDirectionToStr[10];
    char valTempKitToStr[10];
    char valHumKitToStr[10];
    char valQToStr[10];
    char valLevelWaterToStr[10];
} SensorVals;

SensorVals sensorVals;
char jsonPack[JSON_SIZE];
const char* myID = "ND7961";
int loo = 0;

void formatJson(){
    // Xóa mảng dữ liệu
    memset(&sensorVals, 0, sizeof(sensorVals));
    memset(jsonPack, 0, sizeof(jsonPack));

    // Chuyển đổi số sang chuỗi ký tự
    sprintf(sensorVals.valTempToStr, "%d", sensorVals.valTemp);
    sprintf(sensorVals.valHumToStr, "%d", sensorVals.valHum);
    sprintf(sensorVals.valSpeedToStr, "%d", sensorVals.valWindSpeed);
    sprintf(sensorVals.valDirectionToStr, "%d", sensorVals.valWindDirection);
    sprintf(sensorVals.valTempKitToStr, "%d", sensorVals.valTempKit);
    sprintf(sensorVals.valHumKitToStr, "%d", sensorVals.valHumKit);
    sprintf(sensorVals.valQToStr, "%d", sensorVals.valQ);
    sprintf(sensorVals.valLevelWaterToStr, "%d", sensorVals.valLevelWater);

    // Tạo chuỗi JSON
    strcat(jsonPack, "{\"ID\":\"");
    strcat(jsonPack, myID);
    strcat(jsonPack, "\"");

    strcat(jsonPack, ",\"TEMP\":\"");
    strcat(jsonPack, sensorVals.valTempToStr);
    strcat(jsonPack, "\",\"HUM\":\"");
    strcat(jsonPack, sensorVals.valHumToStr);
    strcat(jsonPack, "\"");

    strcat(jsonPack, ",\"WP\":\"");
    strcat(jsonPack, sensorVals.valSpeedToStr);
    strcat(jsonPack, "\",\"WD\":\"");
    strcat(jsonPack, sensorVals.valDirectionToStr);
    strcat(jsonPack, "\",\"TempKit\":\"");
    strcat(jsonPack, sensorVals.valTempKitToStr);
    strcat(jsonPack, "\",\"HumKit\":\"");
    strcat(jsonPack, sensorVals.valHumKitToStr);
    strcat(jsonPack, "\"");

    strcat(jsonPack, ",\"Q\":\"");
    strcat(jsonPack, sensorVals.valQToStr);
    strcat(jsonPack, "\",\"LevelWater\":\"");
    strcat(jsonPack, sensorVals.valLevelWaterToStr);
    strcat(jsonPack, "\"}");

    // In ra chuỗi JSON để kiểm tra
    printf("Generated JSON string: %s\n", jsonPack);

    // Phân tích cú pháp chuỗi JSON
    cJSON *jsonTemp = cJSON_Parse(jsonPack);
    if (!jsonTemp) {
        printf("JSON ERROR!\n");
        const char *error_ptr = cJSON_GetErrorPtr();
        if (error_ptr != NULL) {
            fprintf(stderr, "Error before: %s\n", error_ptr);
        }
    } else {
        printf("JSON OK!\n");
        cJSON_Delete(jsonTemp);  // Đừng quên giải phóng bộ nhớ
    }
}

int main() {
    // Gọi hàm formatJson để kiểm tra
    formatJson();
    return 0;
}
