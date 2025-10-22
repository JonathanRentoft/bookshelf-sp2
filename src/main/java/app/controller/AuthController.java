package app.controller;

import app.dto.AuthResponseDTO;
import app.dto.ErrorDTO;
import app.dto.UserDTO;
import app.service.UserService;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public void registerRoutes(Javalin app) {
        app.post("/api/auth/register", this::register);
        app.post("/api/auth/login", this::login);
    }

    private void register(Context ctx) {
        try {
            UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

            if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Username is required"));
                return;
            }

            if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Password is required"));
                return;
            }

            UserDTO response = userService.register(userDTO);
            ctx.status(201).json(response);
        } catch (Exception e) {
            if (e.getMessage().equals("Username already exists")) {
                ctx.status(400).json(new ErrorDTO(e.getMessage()));
            } else {
                ctx.status(500).json(new ErrorDTO("Internal server error"));
            }
        }
    }

    private void login(Context ctx) {
        try {
            UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

            if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Username is required"));
                return;
            }

            if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                ctx.status(400).json(new ErrorDTO("Password is required"));
                return;
            }

            AuthResponseDTO response = userService.login(userDTO);
            ctx.status(200).json(response);
        } catch (Exception e) {
            if (e.getMessage().equals("Invalid username or password")) {
                ctx.status(401).json(new ErrorDTO(e.getMessage()));
            } else {
                ctx.status(500).json(new ErrorDTO("Internal server error"));
            }
        }
    }
}