#include <stdio.h>

// Hàm sắp xếp mảng con theo thứ tự tăng dần
void sort(int arr[], int n) {
    for (int i = 0; i < n-1; i++) {
        for (int j = i+1; j < n; j++) {
            if (arr[i] > arr[j]) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
    }
}

// Hàm áp dụng bộ lọc trung vị
int median_filter(int window[], int size) {
    sort(window, size);
    // Trả về giá trị trung vị
    return window[size / 2];
}

int main() {
    // Mảng dữ liệu cảm biến
    int sensor_data[] = {10, 12, 15, 14, 11, 17, 18, 16, 15};
    int data_size = sizeof(sensor_data) / sizeof(sensor_data[0]);

    // Kích thước của cửa sổ bộ lọc
    int window_size = 3;
    int filtered_data[data_size];

    // Áp dụng bộ lọc trung vị
    for (int i = 0; i < data_size - window_size + 1; i++) {
        int window[window_size];
        for (int j = 0; j < window_size; j++) {
            window[j] = sensor_data[i + j];
        }
        filtered_data[i + window_size / 2] = median_filter(window, window_size);
    }

    // In ra dữ liệu đã được lọc
    printf("Dữ liệu cảm biến sau khi lọc trung vị:\n");
    for (int i = 0; i < data_size; i++) {
        printf("%d ", filtered_data[i]);
    }
    printf("\n");

    return 0;
}
