package com.ventas.reader;

import com.ventas.model.VentaRaw;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlReader {

    public List<VentaRaw> leer(Path ruta)
            throws IOException {

        List<VentaRaw> ventas = new ArrayList<>();

        try {
            DocumentBuilderFactory factory
                    = DocumentBuilderFactory.newInstance();

            // Evita que el XML cargue entidades externas.
            factory.setFeature(
                    "http://apache.org/xml/features/"
                    + "disallow-doctype-decl", true
            );

            factory.setFeature(
                    XMLConstants.FEATURE_SECURE_PROCESSING,
                    true
            );

            factory.setAttribute(
                    XMLConstants.ACCESS_EXTERNAL_DTD, ""
            );

            factory.setAttribute(
                    XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""
            );

            DocumentBuilder builder
                    = factory.newDocumentBuilder();

            Document documento
                    = builder.parse(ruta.toFile());

            Element raiz = documento.getDocumentElement();

            NodeList registros
                    = raiz.getElementsByTagName("venta");

            int numeroRegistro = 1;

            for (int i = 0; i < registros.getLength(); i++) {

                Node nodo = registros.item(i);

                if (!(nodo instanceof Element elemento)) {
                    continue;
                }

                VentaRaw venta = new VentaRaw(
                        obtener(elemento, "id_venta"),
                        obtener(elemento, "fecha"),
                        obtener(elemento, "sucursal"),
                        obtener(elemento, "codigo_producto"),
                        obtener(elemento, "producto"),
                        obtener(elemento, "cantidad"),
                        obtener(elemento, "precio_unitario"),
                        obtener(elemento, "vendedor"),
                        ruta.getFileName().toString(),
                        numeroRegistro
                );

                ventas.add(venta);
                numeroRegistro++;
            }

            return ventas;

        } catch (ParserConfigurationException
                | SAXException e) {

            throw new IOException(
                    "Error al interpretar XML: "
                    + e.getMessage(), e
            );
        }
    }

    private String obtener(Element elemento, String campo) {

        NodeList hijos = elemento.getChildNodes();

        for (int i = 0; i < hijos.getLength(); i++) {

            Node nodo = hijos.item(i);

            if (nodo.getNodeType()
                    != Node.ELEMENT_NODE) {
                continue;
            }

            String nombre = nodo.getNodeName()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("[\\s-]+", "_");

            if (nombre.equals(campo)) {
                return nodo.getTextContent();
            }
        }

        return null;
    }
}
