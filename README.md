
# Sistema de mensajería en tiempo real

Hecho en JAVA, utilizando sockets. Para ésta nueva versión se agregó disponibilidad como la redundancia pasiva, monitoreo, heartbeats, replicación en momentos críticos y cada x segundos y reintentos de reconexión por parte del cliente.


## Instalación

Descargar los .jar desde la sección de [Releases](https://github.com/Whejseider/TPAYD2/releases)

[MONITOR](https://github.com/Whejseider/TPAYD2/releases/download/3.0/monitor-3.0-SNAPSHOT-jar-with-dependencies.jar)
[SERVIDOR](https://github.com/Whejseider/TPAYD2/releases/download/3.0/servidor-3.0-SNAPSHOT-jar-with-dependencies.jar)
[CLIENTE](https://github.com/Whejseider/TPAYD2/releases/download/3.0/cliente-3.0-SNAPSHOT-jar-with-dependencies.jar)

El Puerto para Primario se utiliza para que el servidor sea primario, ya sea al inicio, o promoviendolo (secundario a primario).

El puerto interno, es para recibir comandos del monitor

El puerto de replicación es para replicar el estado del primario al secundario, puede ser igual que el del puerto interno

Luego el monitor tiene un puerto para escuchar consultas del cliente sobre quién es el servidor primario (puerto 1237, interno en el programa)

1. Iniciar el monitor y hacerle click al botón Iniciar Monitor

![Imágen de monitor](https://github.com/Whejseider/TPAYD2/blob/master/images/monitor.png)

2. Iniciar 2 (dos) instancias de servidor

![Imágen de servidor](https://github.com/Whejseider/TPAYD2/blob/master/images/servidor.png)
- Servidor primario:

  Inicializarlo con los valores default.

  Para cambiar el puerto por el cual será el primario, basta con modificar el textbox
  *"Puerto para primario"* con algún valor diferente a 1235, 1236, 1237. El valor por defecto es 1234.

  Una vez configurado el servidor primario, hacerle click al botón Iniciar Servidor

- Servidor secundario:

  Similar al primario, pero para este caso debemos modificar el puerto *"Puerto para primario"* por algún valor diferente a los puertos 1235, 1236, 1237 y además diferente al puerto del primario.

  Si el primario está en el puerto 1234, podríamos utilizar el puerto 1238.
  Esto es para que cuando se necesite promover el secundario a primario, y utilizará el puerto 1238 para escuchar a los clientes.

  Una vez configurado el servidor secundario, hacerle click al botón Iniciar Servidor

3. Ejecutar el cliente