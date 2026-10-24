# Лабораторная работа № 5. Сборка проекта с Maven и модульное тестирование с JUnit

ФИО: Шилов Никита Дмитриевич
Группа: КБ-251

## Что сделано

Проект лабораторной № 4 переведён на Maven,
добавлены Maven Wrapper и модульные тесты классов Circle, Rectangle и Cylinder.

## Команды

    ./mvnw test
    ./mvnw clean package
    java -jar target/geometry-lab.jar data/figures.txt target/out.bin 5


## Проверка обнаружения ошибки

В `Circle.getArea()` временно заменил `Math.PI * radius * radius`
на `Math.PI * radius`. Тест `CircleTest.areaForGivenRadius` упал,
`./mvnw clean package` завершился с BUILD FAILURE. После возврата
формулы все тесты проходят, сборка успешна.