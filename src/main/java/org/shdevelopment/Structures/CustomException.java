package org.shdevelopment.Structures;

public abstract class CustomException extends Exception {

    public abstract String getUserInfoMessage();

    public static class NoIPV4 extends CustomException {

        public NoIPV4() {
            super();
        }

        @Override
        public String getUserInfoMessage() {
            return "No se ha podido iniciar la aplicacion ya que no pudimos acceder a una red, por favor compruebe su conexion de red y/o antivirus";
        }
    }

    public static class ErrorDecryptingMessage extends CustomException {

        public ErrorDecryptingMessage() {
            super();
        }

        @Override
        public String getUserInfoMessage() {
            return "Ocurrió un error al desencriptar el mensaje.";
        }
    }

    public static class PortAlreadyInUse extends CustomException {

        public PortAlreadyInUse() {
            super();
        }

        @Override
        public String getUserInfoMessage() {
            return "Uno de los puertos utilizados por esta app está en uso. " +
                    "Verifique los logs para conocer el puerto en uso y cierre el programa que lo esté ocupado";
        }
    }
}