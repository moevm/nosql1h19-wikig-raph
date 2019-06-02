# nosql1h19-wikig-raph
Документация по сборке и развертыванию приложения.
Выполняется в ОС Linux.
1) 	Из репозитория скачать apoc*.jar и поместить в {neo4j_dir}/plugins/
2) 	Скачать проект из репозитория.
3) 	Собрать проект
	$ gradle build
4) 	Запустить сервер
$ java -jar build/libs/wikiparser-0.9.0.jar
5) Перейти по http://localhost:8080/html/index.html. 


Пример работы импорта/экспорта, а так же плотность ребер в категории, можно увидеть в [видео](https://youtu.be/GNcuFzWGFVQ)
