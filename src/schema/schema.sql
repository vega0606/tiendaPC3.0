-- Database creation
CREATE DATABASE IF NOT EXISTS sistema_facturacion_inventario;
USE sistema_facturacion_inventario;

-- Table: Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    rol VARCHAR(50) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    ultimo_acceso TIMESTAMP NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP
);

-- Table: Categorias
CREATE TABLE IF NOT EXISTS categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE
);

-- Table: Productos
CREATE TABLE IF NOT EXISTS productos (
    codigo VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    id_categoria INT,
    stock INT DEFAULT 0,
    stock_minimo INT DEFAULT 5,
    precio_compra DECIMAL(10,2) DEFAULT 0,
    precio_venta DECIMAL(10,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'Activo',
    unidad VARCHAR(20) DEFAULT 'Unidad',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id)
);

-- Table: Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    ruc VARCHAR(50), -- o NIT o documento fiscal
    estado VARCHAR(20) DEFAULT 'Activo',
    fecha_registro DATE DEFAULT (CURRENT_DATE),
    saldo_pendiente DECIMAL(10,2) DEFAULT 0
);

-- Table: Proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id VARCHAR(20) PRIMARY KEY,
    empresa VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    ruc VARCHAR(50), -- o NIT o documento fiscal
    categoria VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'Activo',
    fecha_registro DATE DEFAULT (CURRENT_DATE),
    saldo_pendiente DECIMAL(10,2) DEFAULT 0
);

-- Table: Facturas
CREATE TABLE IF NOT EXISTS facturas (
    numero VARCHAR(20) PRIMARY KEY,
    id_cliente VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    iva DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Emitida',
    id_usuario INT,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Table: Detalle_Facturas
CREATE TABLE IF NOT EXISTS detalle_facturas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_factura VARCHAR(20) NOT NULL,
    codigo_producto VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    iva DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (numero_factura) REFERENCES facturas(numero),
    FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
);

-- Table: Pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    numero VARCHAR(20) PRIMARY KEY,
    id_proveedor VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    fecha_entrega DATE,
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Pendiente',
    id_usuario INT,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Table: Detalle_Pedidos
CREATE TABLE IF NOT EXISTS detalle_pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_pedido VARCHAR(20) NOT NULL,
    codigo_producto VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (numero_pedido) REFERENCES pedidos(numero),
    FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
);

-- Table: Devoluciones
CREATE TABLE IF NOT EXISTS devoluciones (
    id VARCHAR(20) PRIMARY KEY,
    numero_factura VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    motivo VARCHAR(100) NOT NULL,
    detalles TEXT,
    total DECIMAL(10,2) NOT NULL,
    id_usuario INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (numero_factura) REFERENCES facturas(numero),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Table: Detalle_Devoluciones
CREATE TABLE IF NOT EXISTS detalle_devoluciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_devolucion VARCHAR(20) NOT NULL,
    codigo_producto VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_devolucion) REFERENCES devoluciones(id),
    FOREIGN KEY (codigo_producto) REFERENCES productos(codigo)
);

-- Table: Transacciones
CREATE TABLE IF NOT EXISTS transacciones (
    id VARCHAR(20) PRIMARY KEY,
    fecha DATE NOT NULL,
    entidad VARCHAR(100) NOT NULL, -- Nombre del cliente o proveedor
    tipo VARCHAR(20) NOT NULL, -- Venta, Compra, Devolución
    referencia VARCHAR(20) NOT NULL, -- Número de factura o pedido
    total DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Completada',
    id_usuario INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Table: Alertas
CREATE TABLE IF NOT EXISTS alertas (
    id VARCHAR(20) PRIMARY KEY,
    tipo VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha DATE NOT NULL,
    prioridad VARCHAR(20) NOT NULL,
    estado VARCHAR(20) DEFAULT 'Pendiente',
    referencia VARCHAR(100), -- ID relacionado (producto, factura, etc.)
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: Movimientos_Inventario
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_producto VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL, -- Entrada, Salida, Ajuste
    cantidad INT NOT NULL,
    referencia VARCHAR(20), -- Número de factura, pedido, etc.
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT,
    FOREIGN KEY (codigo_producto) REFERENCES productos(codigo),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Insert default administrator user
INSERT INTO usuarios (nombre, username, password, email, rol, activo) 
VALUES ('Administrador', 'admin', 'admin123', 'admin@sistema.com', 'Administrador', TRUE);

-- Insert default categories
INSERT INTO categorias (nombre, descripcion, activo) VALUES 
('Electrónicos', 'Productos electrónicos', TRUE),
('Periféricos', 'Periféricos de computadoras', TRUE),
('Hardware', 'Componentes de hardware', TRUE),
('Software', 'Programas y licencias', TRUE),
('Accesorios', 'Accesorios varios', TRUE);

-- Insert sample products
INSERT INTO productos (codigo, nombre, descripcion, id_categoria, stock, stock_minimo, precio_compra, precio_venta, estado, unidad) VALUES
('001', 'Laptop HP 15"', 'Laptop HP 15 pulgadas Core i5', 1, 12, 5, 450.00, 599.99, 'Activo', 'Unidad'),
('002', 'Monitor LG 24"', 'Monitor LG 24 pulgadas Full HD', 1, 3, 5, 120.00, 189.99, 'Activo', 'Unidad'),
('003', 'Impresora Epson', 'Impresora multifuncional Epson', 2, 8, 3, 85.00, 129.99, 'Activo', 'Unidad'),
('004', 'Teclado Logitech', 'Teclado inalámbrico Logitech', 2, 15, 10, 25.00, 39.99, 'Activo', 'Unidad'),
('005', 'Mouse Inalámbrico', 'Mouse inalámbrico con batería recargable', 2, 20, 10, 12.00, 19.99, 'Activo', 'Unidad');

-- Insert sample clients
INSERT INTO clientes (id, nombre, email, telefono, direccion, ruc, estado) VALUES
('C001', 'Juan Pérez', 'juan@ejemplo.com', '555-1234', 'Calle 123', '10101010', 'Activo'),
('C002', 'María López', 'maria@ejemplo.com', '555-5678', 'Avenida 456', '20202020', 'Activo'),
('C003', 'Carlos Gómez', 'carlos@ejemplo.com', '555-9012', 'Plaza 789', '30303030', 'Inactivo'),
('C004', 'Ana Rodríguez', 'ana@ejemplo.com', '555-3456', 'Bulevar 012', '40404040', 'Activo'),
('C005', 'Pedro Martínez', 'pedro@ejemplo.com', '555-7890', 'Callejón 345', '50505050', 'Activo');

-- Insert sample providers
INSERT INTO proveedores (id, empresa, contacto, email, telefono, direccion, ruc, categoria, estado) VALUES
('P001', 'Electrónicos S.A.', 'Roberto García', 'roberto@electsa.com', '555-2468', 'Av. Industrial 123', '60606060', 'Electrónicos', 'Activo'),
('P002', 'Periféricos C.A.', 'Laura Torres', 'ltorres@perifca.com', '555-1357', 'Calle Comercio 456', '70707070', 'Periféricos', 'Activo'),
('P003', 'Suministros Tech', 'Miguel Díaz', 'mdiaz@sumitech.com', '555-3691', 'Plaza Tecnológica 78', '80808080', 'Varios', 'Activo'),
('P004', 'Importadora Digital', 'Sofía Vargas', 'svargas@impdigital.com', '555-4826', 'Av. Central 90', '90909090', 'Electrónicos', 'Inactivo'),
('P005', 'Tecnología Global', 'Fernando Ruiz', 'fruiz@tecglobal.com', '555-9753', 'Calle Mayorista 321', '10203040', 'Hardware', 'Activo');

-- Insert sample alerts
INSERT INTO alertas (id, tipo, descripcion, fecha, prioridad, estado, referencia) VALUES
('A001', 'Stock', 'Monitor LG 24" bajo stock mínimo (3 unidades)', '2025-03-01', 'Alta', 'Pendiente', '002'),
('A002', 'Pago', 'Factura #F-2358 vence en 2 días', '2025-03-02', 'Media', 'Pendiente', 'F-2358'),
('A003', 'Stock', 'Teclado Logitech bajo stock mínimo (5 unidades)', '2025-03-01', 'Baja', 'Atendida', '004'),
('A004', 'Sistema', 'Copia de seguridad programada', '2025-03-05', 'Media', 'Programada', NULL),
('A005', 'Vencimiento', 'Licencia de software expira en 15 días', '2025-03-15', 'Alta', 'Pendiente', NULL);