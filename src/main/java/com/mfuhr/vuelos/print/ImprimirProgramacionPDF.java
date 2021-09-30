package com.mfuhr.vuelos.print;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.mfuhr.vuelos.models.Vuelo;
import com.mfuhr.vuelos.utils.Dia;
import com.mfuhr.vuelos.utils.Mes;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

@Component("/vuelos/imprimirProgramacionPDF")
public class ImprimirProgramacionPDF extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document doc, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Vuelo> vuelos = (List<Vuelo>) model.get("vuelos");
        LocalDate fechaDesde = (LocalDate) model.get("fechaDesde");
        LocalDate fechaHasta = (LocalDate) model.get("fechaHasta");

        String fechaDesdeString = fechaDesde.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        String fechaHastaString = fechaHasta.format(DateTimeFormatter.ofPattern("d MMM yyyy"));
        // LE COLOCO NOMBRE AL ARCHIVO
        // attachment; => colocandolo antes de filename se descarga el pdf
        // automaticamente en Descargas del cliente
        response.setHeader("Content-Disposition", "filename=\"Programación del ".concat(fechaDesdeString).concat(" al ")
                .concat(fechaHastaString).concat(".pdf\""));

        doc.setMargins(30, 30, 85, 10);
        doc.newPage(); // es necesario crear una nueva pagina para asignarle el margen
        doc.setPageSize(PageSize.A4);

        doc.addTitle(
                "Programación del ".concat(fechaDesdeString).concat(" al ").concat(fechaHastaString).concat(".pdf"));

        HeaderFooter evento = new HeaderFooter();
        writer.setPageEvent(evento);

        // CELDAS ENCABEZADO
        PdfPTable columna = new PdfPTable(3);
        PdfPCell cel = new PdfPCell();

        crearColumnas(doc, cel, columna);

        PdfPTable tablaDia = null;

        // FILA POR CADA DIA
        for (LocalDate fecha = fechaDesde; fechaDesde.isBefore(fechaHasta)
                || fechaDesde.equals(fechaHasta); fecha = fechaDesde = fechaDesde.plusDays(1)) {
            LocalDate diaActual = fecha;

           
            tablaDia = new PdfPTable(3);
            tablaDia.setWidths(new int[] { 1, 2, 2 });
            cel.setHorizontalAlignment(Element.ALIGN_CENTER);

            int nroDia = fecha.getDayOfWeek().getValue();

            Dia diaDelaSemana = Dia.getDiaByInt(nroDia);

            String mesString = "";
            if (fecha.getMonthValue() < 10) {
                mesString = "0".concat(String.valueOf(fecha.getMonthValue()));
            } else {
                mesString = String.valueOf(fecha.getMonthValue());
            }
            Mes mes = Mes.getMesByNro(mesString);

            String diaMes = String.valueOf(fecha.getDayOfMonth()).concat("-")
                    .concat(mes.getNombreMes().substring(0, 3).toUpperCase());

            cel.setPhrase(new Phrase(diaDelaSemana.getValor().substring(0, 3).concat(" ").concat(diaMes),
                    FontFactory.getFont(FontFactory.HELVETICA, 8)));
            cel.setVerticalAlignment(Element.ALIGN_MIDDLE);
            List<Vuelo> vuelosDelDia = vuelos.stream().filter(vuelo -> diaActual.equals(vuelo.getFecha()))
                    .collect(Collectors.toList());

            List<Vuelo> vuelosArribo = vuelosDelDia.stream().filter(vuelo -> vuelo.getHoraArribo() != null)
                    .collect(Collectors.toList());
            List<Vuelo> vuelosSalida = vuelosDelDia.stream().filter(vuelo -> vuelo.getHoraSalida() != null)
                    .collect(Collectors.toList());

            cel.setRowspan(vuelosArribo.size() > vuelosSalida.size() ? vuelosArribo.size() : vuelosSalida.size());
            tablaDia.addCell(cel);
            vuelosArribo.sort((v1, v2) -> v1.getHoraArribo().compareTo(v2.getHoraArribo()));
            vuelosSalida.sort((v1, v2) -> v1.getHoraSalida().compareTo(v2.getHoraSalida()));
            PdfPTable tablaArribos = null;
            PdfPTable tablaSalidas = null;

            boolean encArribo = false;
            boolean encSalida = false;

            List<Vuelo> vuelosOrdenados = this.getVuelosEnlazados(vuelosArribo, vuelosSalida);

            int index = 0;
            for (Vuelo vuelo : vuelosOrdenados) {

                tablaArribos = new PdfPTable(3);
                tablaSalidas = new PdfPTable(3);
                
                if(indexPar(index)){
                    encArribo = crearSubColumnas(encArribo, encSalida, cel, tablaArribos);
                    tablaDia.addCell(cargarVuelos(vuelo, tablaArribos, cel));
                }
                
                if(!indexPar(index)){
                    encSalida = crearSubColumnas(encArribo, encSalida, cel, tablaSalidas);
                    tablaDia.addCell(cargarVuelos(vuelo, tablaSalidas, cel));
                }
                
                index++;
            }

            doc.add(tablaDia);
            // Si es Domingo (nroDia = 7), hace un salto de pagina
            if (nroDia == 7 && !fechaDesde.equals(fechaHasta)) {
                doc.newPage();
                crearColumnas(doc, cel, columna);
            }
        }
    }

    private boolean indexPar(int index){
        return index % 2 == 0;
    }

    private List<Vuelo> getVuelosEnlazados(List<Vuelo> vuelosArribo, List<Vuelo> vuelosSalida) {

        List<Vuelo> list = new ArrayList<Vuelo>();
        Vuelo vueloArr = null;
        Vuelo vueloSal = null;   
        List<Vuelo> listVuelosGuardados = new ArrayList<Vuelo>();
        primerBucle:
        for (int i = 0; i < vuelosArribo.size(); i++) {
            vueloArr = vuelosArribo.get(i);
           
            for (int j = 0; j < vuelosSalida.size(); j++) {
                vueloSal = vuelosSalida.get(j);
                // Si el vuelo ya fue leido y cargado a la planilla continua con el siguiente
                if(comprobarVueloGuardados(vueloSal, listVuelosGuardados)) continue;    
                
                if(validarPosicionLista(i, j)){
                    // si coincide la posicion de los vuelos en las listas, y el vueloSalida sale antes que el vueloArribo
                    if(vueloSal.getHoraSalida().isBefore(vueloArr.getHoraArribo())) {
                        list.add(null);
                        list.add(vueloSal);
                        listVuelosGuardados.add(vueloSal);
                        continue;
                    }
                    
                } else {
                    // si la posicion de los vuelos en las listas no coinciden y el vueloSalida sale antes que el vueloArribo
                    if(vueloSal.getHoraSalida().isBefore(vueloArr.getHoraArribo())) {
                        list.add(null);
                        list.add(vueloSal);
                        listVuelosGuardados.add(vueloSal);
                        continue;
                    }
                }
                // si no es la misma compania salta el vueloSalida    
                if(!validarCompania(vueloArr, vueloSal)) continue;

        
                list.add(vueloArr);
                list.add(vueloSal);
                listVuelosGuardados.add(vueloSal);
                continue primerBucle;
                
   
            }
            // Si no se enlazo la salida correspondiente es porque el vuelo arribado debe pernoctar
            list.add(vueloArr);
            list.add(null);
        }

        return list;
    }

    private boolean comprobarVueloGuardados(Vuelo vuelo, List<Vuelo> list){
        return list.stream().anyMatch( vueloLista -> vueloLista.getNroVuelo().equals(vuelo.getNroVuelo()));
    }

    private boolean validarCompania(Vuelo vueloArr, Vuelo vueloSal){
        return vueloArr.getCompania().equals(vueloSal.getCompania());
    }s
    private boolean validarVuelosCorrelativos(Vuelo vueloArr, Vuelo vueloSal){
        int nroArr = vueloArr.getNro();
        int nroSal = vueloSal.getNro();
        return nroArr + 1 == nroSal || nroArr - 1 == nroSal;
    }

    private boolean validarPosicionLista(int i, int j){
        return i == 0 && j == 0;
    }
    
    private PdfPCell cargarVuelos(Vuelo vuelo, PdfPTable tabla, PdfPCell cel) {
        
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 8);
        if(vuelo == null){
            cel.setPhrase(new Phrase());
            tabla.addCell(cel);
        } else {

            if(vuelo.esGranPorte() != null && vuelo.esGranPorte()){
                cel.setBackgroundColor(Color.ORANGE); 
                cel.setPhrase(new Phrase(vuelo.getNroVuelo(), font)); 
                tabla.addCell(cel);
                 
            } else {
                cel.setPhrase(new Phrase(vuelo.getNroVuelo(), font));
                tabla.addCell(cel);
            } 
            
            if(vuelo.getDestino().equals("USH")){
                cel.setPhrase(new Phrase(vuelo.getOrigen(), font));
                tabla.addCell(cel);
                cel.setPhrase(new Phrase(vuelo.getHoraArribo().toString(), font));
                tabla.addCell(cel);
            } else {
                cel.setPhrase(new Phrase(vuelo.getDestino(), font));
                tabla.addCell(cel);
                cel.setPhrase(new Phrase(vuelo.getHoraSalida().toString(), font));
                tabla.addCell(cel);
            }
        }
        cel.setBackgroundColor(Color.WHITE);
        PdfPCell celVuelo = new PdfPCell(tabla);

        return celVuelo;
    }


    private void crearColumnas(Document doc, PdfPCell cel, PdfPTable columna) {

        columna.setWidths(new int[] { 1, 2, 2 });
        cel.setBackgroundColor(Color.LIGHT_GRAY);
        cel.setHorizontalAlignment(Element.ALIGN_CENTER);
        cel.setPhrase(new Phrase("FECHA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
        columna.addCell(cel);
        cel.setPhrase(new Phrase("ARRIBOS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
        columna.addCell(cel);
        cel.setPhrase(new Phrase("PARTIDAS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
        columna.addCell(cel);

        doc.add(columna);
        cel.setBackgroundColor(null);
        columna.flushContent();
    }

    private boolean crearSubColumnas(boolean encArribo, boolean encSalida, PdfPCell cel, PdfPTable tabla) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        if (!encArribo || !encSalida) {
            cel.setPhrase(new Phrase("Vuelo", font));
            tabla.addCell(cel);
            cel.setPhrase(new Phrase("Ruta", font));
            tabla.addCell(cel);
            cel.setPhrase(new Phrase("Hora", font));
            tabla.addCell(cel);

            if (!encArribo) {
                encArribo = true;
                return encArribo;
            }
            if (!encSalida && encArribo) {
                encSalida = true;
                return encSalida;
            }
        }

        return true;
    }
}

class HeaderFooter extends PdfPageEventHelper {

    URL url = this.getClass().getResource("/static/img/logo-london.png");
    PdfPTable tabla = new PdfPTable(1);
    
    
    private int nroPagina = 1;
    
    @Override
    public void onEndPage(PdfWriter writer, Document doc) {
        Image imagen;
        try {
            imagen = Image.getInstance(url);
            imagen.setWidthPercentage(20);
            PdfPCell cel = new PdfPCell();
            cel.setBorder(Rectangle.NO_BORDER);
            cel.addElement(imagen);

            tabla.addCell(cel);
            tabla.setTotalWidth(400);
            
            tabla.writeSelectedRows(0, 1, 80, 820, writer.getDirectContent());
            //tabla.flushContent();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PdfPTable tablaFooter = new PdfPTable(1);
        tablaFooter.setTotalWidth(140);
        PdfPCell cel = new PdfPCell();
        cel.setHorizontalAlignment(Element.ALIGN_CENTER);
        cel.setPhrase(new Phrase("VUELO DE GRAN ENVERGADURA", FontFactory.getFont(FontFactory.HELVETICA, 8)));
        cel.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cel.setBackgroundColor(Color.ORANGE);
        cel.setBorder(Rectangle.NO_BORDER);
        tablaFooter.addCell(cel);
        tablaFooter.writeSelectedRows(0, 1, 85, 45, writer.getDirectContent());

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,
                new Phrase(
                        "LONDON SUPPLY S.A.C.I.F.I. no se hace responsable de los cambios por parte de las compañías",
                        FontFactory.getFont(FontFactory.HELVETICA, 8)),
                435, 20, 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,
                new Phrase("Página ".concat(String.valueOf(nroPagina)),
                        FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY)),
                530, 20, 0);
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {

    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        nroPagina++;
    }

}
