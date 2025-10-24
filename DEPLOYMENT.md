# Deployment Guide for Bookshelf API

This guide will help you deploy the Bookshelf API to a Digital Ocean Droplet using Docker, Docker Compose, GitHub Actions, and Caddy.

## Prerequisites

1. **GitHub Account** - For version control and CI/CD
2. **Docker Hub Account** - For storing Docker images
3. **Digital Ocean Droplet** - Ubuntu 22.04 LTS recommended
4. **Domain Name** (optional but recommended) - For HTTPS with Caddy

## Step 1: Prepare Your Digital Ocean Droplet

### 1.1 Create a Droplet
- Go to Digital Ocean and create a new Droplet
- Choose Ubuntu 22.04 LTS
- Select at least the $6/month plan (1 GB RAM)
- Add your SSH key

### 1.2 Install Docker and Docker Compose

SSH into your droplet and run:

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo apt install docker-compose -y

# Add user to docker group (optional)
sudo usermod -aG docker $USER
```

### 1.3 Install Caddy

```bash
# Install Caddy
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update
sudo apt install caddy -y
```

## Step 2: Configure GitHub Secrets

Go to your GitHub repository → Settings → Secrets and variables → Actions

Add the following secrets:

- `DOCKER_USERNAME` - Your Docker Hub username
- `DOCKER_PASSWORD` - Your Docker Hub password
- `DROPLET_HOST` - Your droplet's IP address
- `DROPLET_USERNAME` - Usually `root`
- `DROPLET_SSH_KEY` - Your private SSH key

## Step 3: Set Up the Droplet

### 3.1 Create project directory

```bash
mkdir -p /root/bookshelf-api
cd /root/bookshelf-api
```

### 3.2 Create .env file

```bash
nano .env
```

Add:
```
DB_PASSWORD=your_secure_password
DOCKER_USERNAME=your_dockerhub_username
```

### 3.3 Download docker-compose.yml

```bash
# Either clone your repo or manually create docker-compose.yml
git clone https://github.com/your-username/bookshelf-api.git .
# Or copy the docker-compose.yml file manually
```

## Step 4: Configure Caddy for HTTPS

### 4.1 Update DNS Records

Point your domain/subdomain to your droplet's IP address:
- Type: A Record
- Name: bookshelf (or your chosen subdomain)
- Value: Your droplet's IP address

### 4.2 Configure Caddyfile

```bash
sudo nano /etc/caddy/Caddyfile
```

Replace the content with your domain:
```
bookshelf.yourdomain.com {
    reverse_proxy localhost:7070
    encode gzip
    
    log {
        output file /var/log/caddy/bookshelf-api.log
    }
}
```

### 4.3 Restart Caddy

```bash
sudo systemctl restart caddy
sudo systemctl status caddy
```

Caddy will automatically obtain SSL certificates from Let's Encrypt!

## Step 5: Deploy the Application

### 5.1 Start the services

```bash
cd /root/bookshelf-api
docker-compose up -d
```

### 5.2 Check logs

```bash
docker-compose logs -f bookshelf-api
```

### 5.3 Verify deployment

```bash
# Check if services are running
docker-compose ps

# Test locally
curl http://localhost:7070/api/routes

# Test via domain (after DNS propagation)
curl https://bookshelf.yourdomain.com/api/routes
```

## Step 6: Set Up Continuous Deployment

Once you've set up GitHub secrets, every push to the main/master branch will:

1. Run tests
2. Build the application
3. Create a Docker image
4. Push to Docker Hub
5. Deploy to your droplet

Watchtower will automatically pull and restart the container when a new image is available.

## Monitoring and Maintenance

### View logs
```bash
# Application logs
docker-compose logs -f bookshelf-api

# Database logs
docker-compose logs -f postgres

# Caddy logs
sudo journalctl -u caddy -f
```

### Update the application
```bash
cd /root/bookshelf-api
docker-compose pull
docker-compose up -d
```

### Backup database
```bash
docker exec bookshelf-db pg_dump -U postgres bookshelf > backup_$(date +%Y%m%d).sql
```

### Restore database
```bash
docker exec -i bookshelf-db psql -U postgres bookshelf < backup_20240101.sql
```

## Troubleshooting

### Port already in use
```bash
sudo lsof -i :7070
sudo kill -9 <PID>
```

### Database connection issues
```bash
# Check if database is healthy
docker-compose ps

# Restart database
docker-compose restart postgres
```

### Caddy certificate issues
```bash
# Check Caddy logs
sudo journalctl -u caddy --no-pager -n 50

# Force certificate renewal
sudo caddy reload --config /etc/caddy/Caddyfile
```

## API Endpoints

Once deployed, your API will be available at:

- `https://bookshelf.yourdomain.com/api/routes` - Route overview (public)
- `https://bookshelf.yourdomain.com/api/auth/register` - Register new user
- `https://bookshelf.yourdomain.com/api/auth/login` - Login
- `https://bookshelf.yourdomain.com/api/books` - Book CRUD operations (requires JWT)

## Security Recommendations

1. **Change default passwords** in `.env` file
2. **Enable firewall** on your droplet:
   ```bash
   sudo ufw allow 22/tcp
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   sudo ufw enable
   ```
3. **Regular updates**:
   ```bash
   sudo apt update && sudo apt upgrade -y
   ```
4. **Backup regularly** - Set up automated database backups
5. **Monitor logs** - Check for suspicious activity

## Support

For issues or questions:
- Check the logs first
- Review the GitHub Actions workflow runs
- Verify DNS settings
- Ensure all secrets are correctly set in GitHub

Happy deploying! 🚀
