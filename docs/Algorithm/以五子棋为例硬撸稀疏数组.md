# 以五子棋为例硬撸稀疏数组

## 案例
五子棋程序，只有两种颜色的棋子，连成五个就gg。可能一个棋盘能放下100多枚棋子，但是总占用空间最少可以只用9个，就能使游戏结束。如果将这盘游戏落盘的话，空间浪费将很大。这时稀疏数组就派上了用场。

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/49fb9b8e4bf2495aa3ed535d281a0bc0~tplv-k3u1fbpfcp-watermark.image)

## 稀疏数组应用场景
当一个数组中大部分元素都是0（或是同一个值）的时候，就可以用稀疏数组来保存此数组。

## 什么是稀疏数组

### 提问
如果是你，你将会如何实现五子棋游戏？  
棋盘--》二维数组  
棋子--》二维数组的下标

### 图解
先有个原始二维数组，10*10的规模，如下

![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/422966b6f07f4bb7aee368d34771f760~tplv-k3u1fbpfcp-watermark.image)

### 分析
明显发现`空值`的数量远大于`有效数据`（这里非空值代表有效数据）。而且是个数组形式，所以稀疏数组来了！拆解成下稀疏数组有如下几点：

- 先统计出原始数组的总行数、总列数、总有效数据数作为稀疏数组的第一行。
- 将有效数据的行列的位置以及数值存储到稀疏数组中。

比如上面的表格换成稀疏数组的存法就如下：

![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/687efa330def475daaec80ae407ae985~tplv-k3u1fbpfcp-watermark.image)

### 结论
- 稀疏数组永远都是三列
- 原始数组存法的话需要10*10=100个元素空间
- 稀疏数组存法的话需要11*3=33个元素空间，效果很明显

### 代码实现

```java
private static void sparseArray(int[][] chessArray, int row, int col) {
    // 棋盘内棋子个数
    int sum = 0;
    System.out.println("原始二维数组");
    for (int i = 0; i < row; i++) {
        System.out.println();
        for (int j = 0; j < col; j++) {
            System.out.printf("%d  ", chessArray[i][j]);
            // 此位置有棋子
            if (chessArray[i][j] != 0) {
                sum++;
            }
        }
    }

    // 得到棋子个数
    System.out.println("\n\n棋子个数：" + sum);

    // 生成稀疏数组，其列长度固定为3
    int[][] spaserArray = new int[sum + 1][3];
    // 稀疏数组第一行
    spaserArray[0][0] = row;
    spaserArray[0][1] = col;
    spaserArray[0][2] = sum;

    // 稀疏数组的行数
    int count = 0;
    // 添加棋子到稀疏数组
    for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
            if (chessArray[i][j] != 0) {
                count++;
                spaserArray[count][0] = i;
                spaserArray[count][1] = j;
                spaserArray[count][2] = chessArray[i][j];
            }
        }
    }

    System.out.println("\n\n原始二维数组压缩成的稀疏数组");
    for (int i = 0; i < spaserArray.length; i++) {
        System.out.println();
        for (int j = 0; j < 3; j++) {
            System.out.printf("%d  ", spaserArray[i][j]);
        }
    }

    // 稀疏数组恢复成原始二维数组
    int[][] originArray = new int[spaserArray[0][0]][spaserArray[0][1]];
    for (int i = 1; i < spaserArray.length; i++) {
        originArray[spaserArray[i][0]][spaserArray[i][1]] = spaserArray[i][2];
    }

    System.out.println();
    System.out.println("\n\n稀疏数组恢复成原始二维数组");
    for (int i = 0; i < originArray.length; i++) {
        System.out.println();
        for (int j = 0; j < originArray[i].length; j++) {
            System.out.printf("%d  ", originArray[i][j]);
        }
    }
}
```
### 运行代码
```java
public static void main(String[] args) {
    // 生成原始二维数组
    int row = 10;
    int col = 10;
    int[][] chessArray = new int[row][col];
    // 初始化棋子位置
    chessArray[1][3] = 1;
    chessArray[2][3] = 2;
    chessArray[2][4] = 1;
    chessArray[3][4] = 2;
    chessArray[3][5] = 1;
    chessArray[4][5] = 1;
    chessArray[4][6] = 2;
    chessArray[5][4] = 1;
    chessArray[5][5] = 2;
    chessArray[6][4] = 2;
    sparseArray(chessArray, row, col);
}
```

### 运行结果
```java
原始二维数组

0  0  0  0  0  0  0  0  0  0  
0  0  0  1  0  0  0  0  0  0  
0  0  0  2  1  0  0  0  0  0  
0  0  0  0  2  1  0  0  0  0  
0  0  0  0  0  1  2  0  0  0  
0  0  0  0  1  2  0  0  0  0  
0  0  0  0  2  0  0  0  0  0  
0  0  0  0  0  0  0  0  0  0  
0  0  0  0  0  0  0  0  0  0  
0  0  0  0  0  0  0  0  0  0  

棋子个数：10


原始二维数组压缩成的稀疏数组

10  10  10  
1  3  1  
2  3  2  
2  4  1  
3  4  2  
3  5  1  
4  5  1  
4  6  2  
5  4  1  
5  5  2  
6  4  2  


稀疏数组恢复成原始二维数组

0  0  0  0  0  0  0  0  0  0  
0  0  0  1  0  0  0  0  0  0  
0  0  0  2  1  0  0  0  0  0  
0  0  0  0  2  1  0  0  0  0  
0  0  0  0  0  1  2  0  0  0  
0  0  0  0  1  2  0  0  0  0  
0  0  0  0  2  0  0  0  0  0  
0  0  0  0  0  0  0  0  0  0  
0  0  0  0  0  0  0  0  0  0  
0  0  0  0  0  0  0  0  0  0  
```