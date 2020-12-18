### Руководство  
1) Задать переменные окружения INSTAGRAM_LOGIN, INSTAGRAM_PASSWORD на свои значения  
2) Деплой системы: ./vm_build.sh deploy
3) Старт приложения считывания логов: ./vm_build.sh start
4) Help: ./vm_build.sh help
5) Запуск батча на считывание логов:
POST -X http://localhost:8080/run/{instgramProfileName}  
6) Просмотр результатов в кибане http://localhost:5601