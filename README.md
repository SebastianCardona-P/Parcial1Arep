
# Parcial Arep 1

## Autor Sebastian Cardona

## Intalar y ejecutar el proyecto

### Clonar el repositorio:

git clone https://github.com/SebastianCardona-P/Parcial1Arep

cd Parcial1Arep

### una vez dentro compilar:

mvn clean install

### Ejecute el HttpServer:
java -cp target/classes co.edu.escuelaing.parcial1corte.HttpServer

esto corre el backend en el puerto 35000

## Ejecute el Facade
en otra terminal

java -cp target/classes co.edu.escuelaing.parcial1corte.Facade

esto arrancará el server de facade en http://localhost:36000/

abra http://localhost:36000/ o http://localhost:36000/cliente para ver el cliente con HTML y JS
![inicio](image.png)

### empiece a probar

### Añadir un numero al linkedList 
![alt text](image-1.png)
![alt text](image-3.png)
![alt text](image-4.png)

### Listar los numeros
![alt text](image-5.png)

### limpiar la lista
![alt text](image-6.png)


### calcular las estadísticas

lista vacia:

![alt text](image-7.png)

Normal:

![alt text](image-8.png)

otro ejemplo
![alt text](image-9.png)