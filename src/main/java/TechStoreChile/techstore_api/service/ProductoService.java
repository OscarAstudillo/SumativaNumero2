package TechStoreChile.techstore_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TechStoreChile.techstore_api.dto.ProductoDTO;
import TechStoreChile.techstore_api.model.Producto;
import TechStoreChile.techstore_api.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

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
        return productoRepository.save(producto);
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
            return productoRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // Cambiar el estado a inactivo en lugar de eliminar físicamente el producto
    public void eliminarProducto(Long id) {
        productoRepository.findById(id).ifPresent(p -> {
        p.setActivo(false); // Cambiamos el estado a inactivo
        productoRepository.save(p);
        });
    }
}
