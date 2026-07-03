package TechStoreChile.techstore_api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import TechStoreChile.techstore_api.dto.ProductoDTO;
import TechStoreChile.techstore_api.model.Producto;
import TechStoreChile.techstore_api.repository.ProductoRepository;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private SqsClient sqsClient;

    private final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/556437028339/techstore-audit-queue";

    private void enviarAuditoriaSQS(String accion, Producto producto) {
        // Obtener el correo del JWT autenticado
        String usuario = SecurityContextHolder.getContext().getAuthentication().getName();
        String fecha = Instant.now().toString();

        // Armar el JSON exacto requerido en la evaluación
        String jsonBody = String.format(
            "{\"accion\": \"%s\", \"productoId\": %d, \"nombre\": \"%s\", \"usuario\": \"%s\", \"fecha\": \"%s\"}",
            accion, producto.getId(), producto.getNombre(), usuario, fecha
        );

        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
            .queueUrl(QUEUE_URL)
            .messageBody(jsonBody)
            .build();
        sqsClient.sendMessage(sendMsgRequest);
    }

    // Listar todos los productos.
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    //Llama a un producto por Id
    public Producto getProductoById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Crea un producto usando el DTO
    public Producto crearProducto(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        Producto guardado = productoRepository.save(producto);
        enviarAuditoriaSQS("CREAR", guardado); // Disparar evento
        return guardado;
    }

    // Modificar un producto existente usando el DTO
    public Producto modificarProducto(Long id, ProductoDTO dto) {
        return productoRepository.findById(id).map(p -> {
            p.setNombre(dto.getNombre());
            p.setDescripcion(dto.getDescripcion());
            p.setPrecio(dto.getPrecio());
            p.setStock(dto.getStock());
            p.setCategoria(dto.getCategoria());
            p.setActivo(dto.getActivo());
            Producto modificado = productoRepository.save(p);
            enviarAuditoriaSQS("MODIFICAR", modificado); // Disparar evento
            return modificado;
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // Cambiar el estado a inactivo en lugar de eliminar físicamente el producto
    public void eliminarProducto(Long id) {
        productoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false); // Cambiamos el estado a inactivo
            Producto eliminado = productoRepository.save(p);
            enviarAuditoriaSQS("ELIMINAR", eliminado);
        });
    }
}
