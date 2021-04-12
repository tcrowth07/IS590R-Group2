terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
  }
}

# Configure the AWS Provider
# Command Line commands for setting them up as environment variables
#Mac
# export AWS_ACCESS_KEY_ID="AKIATDKSGQGUTZM5MAVH"
# export AWS_SECRET_ACCESS_KEY_ID="GZHKbjToZ7IYrDl64KFvonbPj5WObe5uvtUM"
# export AWS_DEFAULT_REGION="us-west-2"

#Windows
# $env:AWS_ACCESS_KEY_ID="<<insert Access Key>>"
# $env:AWS_SECRET_ACCESS_KEY_ID="insert Secret Access Key"
# $env:AWS_DEFAULT_REGION="us-west-2"
provider "aws" {
  region = "us-west-2"
  access_key = "<replace this>"
  secret_key = "<replace this>"
}

//
//resource "aws_instance" "web-server-instance" {
//  ami = "ami-0928f4202481dfdf6"
//  instance_type = "t2.micro"
//  availability_zone = "us-west-2a"
//  key_name = "main-key"
//
//  network_interface {
//    device_index = 0
//    network_interface_id = aws_network_interface.web-server-nic.id
//  }
//
//  user_data = <<-EOF
//              #!/bin/bash
//              sudo apt update -y
//              sudo apt install apache2 -y
//              sudo systemctl start apache2
//              sudo bash -c 'echo your very first web server > /var/www/html/index.html'
//              EOF
//
//  tags = {
//    Name: "IS590R-Ubuntu"
//  }
//}


#terraform init
#terraform plan
#terraform apply
#terraform destroy #destroys all infrastructure - Don't use without parameters. Rather comment stuff out and just use terraform apply
#terraform destroy -target aws_instanct.web-server-instance
#terraform state list #shows all the resources
#terraform state show aws_vpc.main-vpc
#terraform output
#terraform refresh
#terraform apply -target aws_instanct.web-server-instance
#terraform apply -var-file terraform.tfvars

//output "server-public-ip" {
//  value = aws_instance.web-server-instance.public_ip
//}

//variable "subnet-prefix" {
//  description = "cidr block for subnet" #optional
//  default = 10.0.1.0/24 #optional
//  #type = string #optional
//}

#reference the variable by using var.subnet-prefix


# Create RDS
//resource "aws_db_instance" "postgres" {
//  engine               = "postgresql"
//  instance_class       = "db.t2.micro"
//  name                 = "postgres"
//  username             = "docker"
//  password             = "dockerrocks!"
//  allocated_storage     = 5
//  max_allocated_storage = 15
//}


resource "aws_lb" "my-ecs-lb" {
  name = "my-ecs-lb"
  load_balancer_type = "application"
  internal = false
  subnets = [aws_subnet.subnet-1]
  security_groups = [aws_security_group.lb]
}


resource "aws_security_group" "lb" {
  name = "allow-all-lb"
  vpc_id = aws_vpc.main-vpc.id
  ingress {
    from_port = 0
    protocol = "-1"
    to_port = 0
    cidr_blocks = [0.0.0.0/0]
  }
  egress {
    from_port = 0
    protocol = "-1"
    to_port = 0
    cidr_blocks = [0.0.0.0/0]
  }
}



#Create ECS
resource "aws_ecs_service" "service" {
  name            = "backend-service"
  cluster = aws_ecs_cluster.web-cluster.id #This needs to be changed
  task_definition = aws_ecs_task_definition.task-definition-test.arn
  desired_count   = 1

  #not sure if this block is required
//  ordered_placement_strategy {
//    type = "binpack"
//    field = "cpu"
//  }

  load_balancer {
    target_group_arn = aws_lb_target_group.lb_target_group.arn
    container_name = "is590rContainer" #replace this with whatever we call our container
    container_port = 8080 # not sure if this is the right port or not
  }
  launch_type = "EC2"
  depends_on = [aws_lb_listener.web_listener] #this might can be deleted
}


#Create ECR
resource "aws_ecr_repository" "is590r" {
  name = "is590r"
}

resource "aws_ecr_lifecycle_policy" "postgrespolicy" {
  repository = aws_ecr_repository.is590r.name

  policy = <<EOF
  {
      "rules": [
          {
              "rulePriority": 1,
              "description": "Expire JournalEntries older than 14 days",
              "selection": {
                  "tagStatus": "untagged",
                  "countType": "sinceImagePushed",
                  "countUnit": "days",
                  "countNumber": 14
              },
              "action": {
                  "type": "expire"
              }
          }
      ]
  }
EOF
}



# Create a VPC
resource "aws_vpc" "main-vpc" {
  cidr_block = "10.0.0.0/16"
  tage = {
    Name = "Production"
  }
}


#Create an Internet Gateway

resource "aws_internet_gateway" "main-gateway" {
  vpc_id = aws_vpc.main-vpc.id
  tags = {
    Name = "Prod-Gateway"
  }
}

#Create a Route Table
resource "aws_route_table" "prod-route-table"{
  vpc_id = aws_vpc.main-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main-gateway.id
  }

  route {
    ipv6_cidr_block = "::/0"
    gateway_id = aws_internet_gateway.main-gateway.id
  }

  tags = {
    Name = "Prod-Route_Table"
  }
}


#Create a Subnet

resource "aws_subnet" "subnet-1" {
  vpc_id = aws_vpc.main-vpc.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "us-west-2a"
  tags = {
    Name= "Prod-public-subnet1"
  }
}


#Create another Subnet
//
resource "aws_subnet" "subnet-1" {
  vpc_id = aws_vpc.main-vpc.id
  cidr_block = "10.0.200.0/24"
  availability_zone = "us-west-2a"
  tags = {
    Name= "Prod-public-subnet1"
  }
}



#Associate subnet with routing table
resource "aws_route_table_association" "a" {
  subnet_id = aws_subnet.subnet-1.id
  route_table_id = aws_route_table.prod-route-table.id
}

#Create a security group
resource "aws_security_group" "allow_web" {
  name = "allow-web-traffic"
  description = "Allow web inbound traffic"
  vpc_id = aws_vpc.main-vpc.id

  ingress {
    description = "HTTPS"
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = [0.0.0.0/0]
  }
  ingress {
    description = "HTTP"
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = [0.0.0.0/0]
  }
  ingress {
    description = "SSH"
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = [0.0.0.0/0]
  }
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = [0.0.0.0/0]
  }

  tags = {
    Name = "allow-web"
  }
}

#Create Network Interface
//resource "aws_network_interface" "web-server-nic" {
//  subnet_id = aws_subnet.subnet-1.id
//  private_ips = ["10.0.1.50"]
//  security_groups = [aws_security_group.allow_web.id]
//}
//
//#Create elastic IP
//resource "aws_eip" "one" {
//  vpc = true
//  network_interface = aws_network_interface.web-server-nic.id
//  associate_with_private_ip = "10.0.1.50"
//  depends_on = [aws_internet_gateway.main-gateway]
//}



#chmod 400 main-key.pem
#ssh -i main-key.pem ubuntu@<IP Address> #Possibly IS590R-Ubuntu...

