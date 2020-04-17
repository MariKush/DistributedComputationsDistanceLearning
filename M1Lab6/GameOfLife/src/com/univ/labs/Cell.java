package com.univ.labs;

import java.util.concurrent.locks.ReentrantReadWriteLock;
/*
Блокування для читання (read-lock) може утримувати будь-яку кількість потоків до тих пір,
поки не утримує блокування для запису (write-lock).
Такий підхід може збільшити продуктивність в разі, коли читання використовується набагато частіше, ніж запис.
*/

// Клітинка автомату
class Cell {
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();//недобросовісне
    byte value = 0;
}
