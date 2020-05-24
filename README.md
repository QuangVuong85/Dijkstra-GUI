## HƯỚNG DẪN THỰC HIỆN DỰ ÁN HỌC PHẦN “THỰC TẬP CNTT”
## Chủ đề dự án:
```
Cho một tập hợp các điểm trên mặt phẳng tọa độ Oxy, mỗi điểm có một tọa độ (x,y). Giữa các điểm có thể có đường nối hoặc không, đường nối có thể một chiều hoặc hai chiều.
Cho hai điểm bất kỳ, cần tìm lộ trình ngắn nhất nối hai điểm với nhau. Khoảng cách giữa 2 điểm được tính theo khoảng cách hình học giữa 2 điểm trên mặt phẳng.
```
## Yêu cầu: 
```
Viết chương trình có cửa sổ GUI, thể hiện trực quan (đồ họa) các điểm, các đường nối trên mặt phẳng.
Hai điểm được cho bất kỳ, với lộ trình tìm được, cần thể hiện trực quan lộ trình đó (highlight, đổi màu vẽ).
Chương trình cần sinh ngẫu nhiên các điểm trên mặt phẳng và sinh ngẫu nhiên đường nối giữa các điểm.
```
## Gợi ý:
```
Chương trình này mô phỏng thuật toán tìm đường đi ngắn nhất trên đồ thị, có thể dùng thuật toán Bellman – Ford hoặc Dijkstra để giải.
Trên giao diện cần có vùng nhập số lượng điểm (nhỏ hơn 20), số lượng đường (nhỏ hơn 400). Từ hai tham số này sinh ngẫu nhiên tọa độ các điểm cùng đường nối các điểm. Tọa độ (tung độ, hoành độ) các điểm là số nguyên và nằm trong khoảng [-100, 100]. Mỗi điểm được vẽ trên mặt phẳng dưới dạng một hình tròn với bán kính đủ lớn để nhìn thấy được.
Trên giao diện cũng cần có vùng nhập điểm đầu và điểm cuối của lộ trình (hoặc có thể click chuột vào các điểm để xác định).
Sau khi tìm được lộ trình ngắn nhất nối 2 điểm đã chọn, cần vẽ lộ trình đó một cách nổi bật so với các đường nối các điểm (Giống Google Map chỉ đường).
```
## Ngôn ngữ phát triển:
* Java + JavaFX 
## Website:
Link: [http://monkeyfamily.tech](http://monkeyfamily.tech)
## Kết quả:
![image.test.25.05](https://github.com/QuangVuong85/Dijkstra-GUI/blob/master/images/graph.dijkstra.25.05.test.png)
