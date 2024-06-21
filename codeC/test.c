#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "cJSON.h"

// Hàm kiểm tra giá trị của mục "ID" trong JSON có bằng "ND6961" không
int check_id_value(const char *json_str, const char *expected_value);

int main() {
    // Chuỗi JSON đầu vào
    const char *json_str = "{\"ID\":\"ND6961\", \"TEMP\":43}";

    // Kiểm tra giá trị của mục "ID"
    if (check_id_value(json_str, "ND6961")) {
        printf("The ID value is ND6961.\n");
    } else {
        printf("The ID value is not ND6961.\n");
    }

    return 0;
}

int check_id_value(const char *json_str, const char *expected_value) {
    // Phân tích cú pháp chuỗi JSON
    cJSON *root = cJSON_Parse(json_str);
    if (root == NULL) {
        const char *error_ptr = cJSON_GetErrorPtr();
        if (error_ptr != NULL) {
            fprintf(stderr, "Error before: %s\n", error_ptr);
        }
        return 0;
    }

    // Lấy mục "ID"
    cJSON *id_item = cJSON_GetObjectItemCaseSensitive(root, "ID");
    if (cJSON_IsString(id_item) && (id_item->valuestring != NULL)) {
        // Kiểm tra giá trị của mục "ID"
        if (strcmp(id_item->valuestring, expected_value) == 0) {
            cJSON_Delete(root); // Giải phóng bộ nhớ đã cấp phát cho đối tượng JSON
            return 1; // Giá trị bằng expected_value
        }
    }

    cJSON_Delete(root); // Giải phóng bộ nhớ đã cấp phát cho đối tượng JSON
    return 0; // Giá trị không bằng expected_value hoặc mục "ID" không tồn tại
}
