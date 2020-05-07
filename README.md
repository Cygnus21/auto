﻿**Описание REST API:**

1.`GET /api/autos` - вывести все элементы

   варианты:
   - с использованием сортировки `GET /api/autos?sort=color`
   - с использованием фильтрации `GET /api/autos?filter=color:red`

   Одновременное использование сортировки и фильтрации на данный момент не поддерживается.


2.`POST /api/autos/add`  - добавление нового автомобиля

JSON должен быть следующего вида:

```
{
"number":"s755os",
"make":"Shevrale",
"color":"yellow",
"year":2000,
"vin": "LYJUI54389877323"
}
```

Вернет `200 Ок "Auto was successfully added"`, если добавление было успешным

Вернет ошибку `400 "Auto with vim already exists in database" `
если данный элемент существует в базе (проверка по vin, у каждой машины он уникальный)

3.`DELETE  /api/autos/delete/:id` - удаление автомобиля по его id

Вернет ошибку `404 "Auto was not found"`, если автомобиля с указанным id нет в базе

Вернет `200 Ok`, если удаление успешное 

4.`GET /api/autos/stat/count` - статистика, показывающая количество записей в 
базе данных.
Вернет количество записей

5.`GET /api/autos/stat/makeCount` - статистика, показывающая сколько автомобилей 
каждой марки находится в базе.

**Используемые технологии:**
 - Scala
 - postgresql
 - Slick
 - Play 2 framework
 - sbt
 
**Описание классов:**

Auto.scala

Модель Auto для базы данных

AutoService.scala

Обеспечивает взаимосвязь между контроллером и моделью базы данных (Auto)

AutoController.scala

Обеспечивает взаимосвязь между моделью Auto и событиями транспортного уровня
(в том числе, запросы и ответы на них)

**Как запустить приложение:**
 - Клонировать исходный код при помощи git clone https://github.com/Cygnus21/auto.git
 - Перейти в каталог auto
 - Запустить при помощи команды sbt run
 - Для проверки REST API можно использовать Postman (или аналогичные ресурсы)