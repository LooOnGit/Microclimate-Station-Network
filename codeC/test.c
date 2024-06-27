#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "cJSON.h"

char jsonPack[250];

// Hàm gộp hai chuỗi JSON và trả về chuỗi JSON kết quả
char* merge_json_strings(const char *json_str1, const char *json_str2) {
    cJSON *json1 = cJSON_Parse(json_str1);
    cJSON *json2 = cJSON_Parse(json_str2);

    if (json1 == NULL || json2 == NULL) {
        fprintf(stderr, "Error parsing JSON.\n");
        return NULL;
    }

    cJSON *id = cJSON_GetObjectItem(json2, "ID");
    if (id == NULL) {
        fprintf(stderr, "Error: ID not found in json2.\n");
        cJSON_Delete(json1);
        cJSON_Delete(json2);
        return NULL;
    }

    cJSON_AddItemToObject(json1, "TEMP", cJSON_Duplicate(id, 1));

    strcpy(jsonPack,cJSON_Print(json1));
    cJSON_Delete(json1);
    cJSON_Delete(json2);
}

int main() {
    const char *json_str1 = "{\"ID\":\"ND5434\", \"Q\":\"34\"}";
    const char *json_str2 = "{\"ID\":\"ND5435\", \"TEMP\":\"34\"}";

    merge_json_strings(json_str1, json_str2);

    printf("Merged JSON: %s\n", jsonPack);

    return 0;
}
