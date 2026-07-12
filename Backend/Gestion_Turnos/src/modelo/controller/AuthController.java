package modelo.controller;
import config.JwtUtil;
import modelo.dao.UsuarioDao;
import modelo.entidades.Usuario;
import modelo.entidades.UsuarioLoginDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final JwtUtil jwtUtil = new JwtUtil();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDTO loginData) {

        Usuario user = usuarioDao.buscarPorUsername(loginData.getUsername());

        if (user != null && passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {


            String token = jwtUtil.generarToken(user.getUsername());


            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("rol", user.getRol());

            return ResponseEntity.ok(response);

        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
        }
    }
}