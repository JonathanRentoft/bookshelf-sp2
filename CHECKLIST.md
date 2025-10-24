# Deployment Checklist ✅

Use this checklist to ensure everything is set up correctly before deploying.

## Pre-Deployment Checklist

### 1. Local Development ✅
- [x] Application builds successfully (`mvn clean package`)
- [x] All tests pass (`mvn test`)
- [x] Application runs locally
- [x] Can register users
- [x] Can login and get JWT token
- [x] Can perform CRUD operations on books
- [x] Route overview accessible at `/api/routes`

### 2. GitHub Repository ✅
- [ ] Code pushed to GitHub
- [ ] Repository is public or accessible
- [ ] Branch is `main` or `master`
- [ ] `.github/workflows/deploy.yml` is present

### 3. Docker Hub Setup 📦
- [ ] Docker Hub account created
- [ ] Username noted for configuration
- [ ] Password/access token generated

### 4. GitHub Secrets Configuration 🔐
Add these secrets in GitHub: Settings → Secrets and variables → Actions

- [ ] `DOCKER_USERNAME` - Your Docker Hub username
- [ ] `DOCKER_PASSWORD` - Your Docker Hub password or access token
- [ ] `DROPLET_HOST` - Your Digital Ocean droplet IP address
- [ ] `DROPLET_USERNAME` - SSH username (usually `root`)
- [ ] `DROPLET_SSH_KEY` - Your private SSH key (entire key content)

### 5. Digital Ocean Droplet Setup 🌊
- [ ] Droplet created (Ubuntu 22.04 LTS recommended)
- [ ] SSH access configured
- [ ] Docker installed on droplet
- [ ] Docker Compose installed on droplet
- [ ] Caddy installed on droplet (if using HTTPS)

### 6. Droplet Configuration 🔧
SSH into your droplet and complete:

- [ ] Created directory: `/root/bookshelf-api`
- [ ] Created `.env` file with:
  ```
  DB_PASSWORD=your_secure_password
  DOCKER_USERNAME=your_dockerhub_username
  ```
- [ ] Uploaded `docker-compose.yml` to droplet
- [ ] Firewall configured:
  ```bash
  sudo ufw allow 22/tcp   # SSH
  sudo ufw allow 80/tcp   # HTTP
  sudo ufw allow 443/tcp  # HTTPS
  sudo ufw enable
  ```

### 7. Domain & DNS (Optional for HTTPS) 🌐
- [ ] Domain purchased or subdomain configured
- [ ] DNS A record pointing to droplet IP
- [ ] DNS propagation completed (check with `nslookup yourdomain.com`)
- [ ] Caddyfile updated with your domain
- [ ] Caddy restarted: `sudo systemctl restart caddy`

### 8. Initial Deployment 🚀
- [ ] Pushed code to GitHub `main` or `master` branch
- [ ] GitHub Actions workflow triggered
- [ ] Build completed successfully
- [ ] Tests passed
- [ ] Docker image pushed to Docker Hub
- [ ] Deployment to droplet successful

### 9. Verification ✅
Test these on your deployed API:

- [ ] Route overview accessible: `https://yourdomain.com/api/routes`
- [ ] Can register a user: `POST /api/auth/register`
- [ ] Can login: `POST /api/auth/login`
- [ ] Can create a book: `POST /api/books` (with JWT)
- [ ] Can get books: `GET /api/books` (with JWT)
- [ ] HTTPS working (green lock in browser)
- [ ] Logs are accessible: `docker-compose logs -f`

### 10. Monitoring & Maintenance 📊
- [ ] Watchtower running (auto-updates containers)
- [ ] Database backups configured
- [ ] Logs being monitored
- [ ] SSL certificate auto-renewal working (Caddy handles this)

## Quick Verification Commands

Run these on your droplet:

```bash
# Check containers are running
docker-compose ps

# Check API logs
docker-compose logs -f bookshelf-api

# Check database logs
docker-compose logs -f postgres

# Check Caddy status
sudo systemctl status caddy

# Test API locally on droplet
curl http://localhost:7070/api/routes

# Check disk space
df -h

# Check memory
free -m
```

## Troubleshooting Checklist

If something isn't working:

- [ ] Checked GitHub Actions logs
- [ ] Checked Docker container logs
- [ ] Verified environment variables in `.env`
- [ ] Verified database is running and healthy
- [ ] Verified firewall allows correct ports
- [ ] Verified DNS is pointing to correct IP
- [ ] Checked Caddy configuration and logs
- [ ] Verified Docker Hub credentials are correct
- [ ] Verified droplet has enough resources

## Security Checklist 🔒

- [ ] Changed default database password
- [ ] JWT secret is secure (in code or env var)
- [ ] Firewall is enabled
- [ ] SSH key authentication enabled (password disabled)
- [ ] Regular security updates scheduled
- [ ] Logs monitored for suspicious activity
- [ ] Database backups configured
- [ ] HTTPS enabled and working

## Documentation Checklist 📚

- [ ] README.md updated with correct information
- [ ] Deployment documentation matches your setup
- [ ] Test credentials documented (if any)
- [ ] API endpoints documented
- [ ] Known issues documented
- [ ] Contact information for support

## Hand-in Checklist (for Course) 📝

Ready to submit:

- [ ] GitHub repository URL ready
- [ ] Deployed API URL ready
- [ ] Route overview accessible publicly
- [ ] API documentation submitted on Moodle
- [ ] All tests passing
- [ ] Application running on droplet
- [ ] HTTPS working (if required)
- [ ] Can demonstrate all CRUD operations
- [ ] Can demonstrate authentication
- [ ] Can demonstrate error handling

## API Documentation (Moodle Submission)

Submit these items:

1. **GitHub Repository URL**
   - Example: `https://github.com/yourusername/bookshelf-api`

2. **Deployed API URL**
   - Example: `https://bookshelf.yourdomain.com`

3. **Route Overview URL**
   - Example: `https://bookshelf.yourdomain.com/api/routes`

4. **API Description**
   - Brief description of what the API does
   - Mention: User authentication, book management, JWT security

5. **Test Credentials** (if required)
   - Username: `alice`, Password: `password123`
   - Username: `bob`, Password: `securepass`

## Success Criteria ✨

Your API is ready when:

- ✅ All tests pass locally
- ✅ Application deploys automatically via GitHub Actions
- ✅ HTTPS is working with valid certificate
- ✅ All 7 endpoints are functional
- ✅ Authentication and authorization working
- ✅ Error handling returns correct status codes
- ✅ Route overview is publicly accessible
- ✅ Documentation is complete and accurate

---

**Congratulations!** 🎉 Once all items are checked, your API is production-ready!

For any issues, refer to:
- `QUICKSTART.md` - Quick testing locally
- `TESTING.md` - Running tests
- `DEPLOYMENT.md` - Detailed deployment steps
- `README.md` - General overview
