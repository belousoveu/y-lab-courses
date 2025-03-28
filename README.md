# Инструкция по запуску приложения

## Запуск в docker-контейнере

Склонируйте репозиторий

```bash
git clone https://github.com/belousoveu/y-lab-courses
```

Используйте maven для сборки приложения

```bash
mvn clean package
```

Соберите и запустите контейнеры
```
docker-compose build
docker-compose up
```

Приложение доступно по адресу: `http:\\localhost:8080\`

## Запуск приложения в локальном режиме

Склонируйте репозиторий

```bash
git clone https://github.com/belousoveu/y-lab-courses
```

Используйте maven для сборки приложения

```bash
mvn clean package
```

Перед первым запуском настройте базу данных запустив sql-скрипт
`src/main/resources/db/changelog/changes/init.sql`

Запустите приложение

```bash
java -jar target/PersonalMoneyTracker-1.3-SNAPSHOT-jar-with-dependencies.jar
```

Приложение доступно по адресу: `http:\\localhost:8080\`







