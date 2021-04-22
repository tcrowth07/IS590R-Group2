terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.0"
    }
  }
  backend "s3" {
    bucket = "is590r"
    key    = "state.tfstate"
  }
}

# Configure the AWS Provider
provider "aws" {}



resource "aws_vpc" "vpc" {
  cidr_block = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags       = {
    Name = "Terraform VPC"
  }
}

resource "aws_internet_gateway" "internet_gateway" {
  vpc_id = aws_vpc.vpc.id
}

resource "aws_subnet" "pub_subnet" {
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = "10.0.0.0/24"
  availability_zone = "us-west-2c"
}

resource "aws_subnet" "pub_subnet2" {
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone = "us-west-2a"
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.internet_gateway.id
  }
}

resource "aws_route_table_association" "route_table_association" {
  subnet_id      = aws_subnet.pub_subnet.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "ecs_sg" {
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
  }

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
  }

  ingress {
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 80
    protocol = "tcp"
    to_port = 80
    cidr_blocks     = ["0.0.0.0/0"]
  }

  egress {
    from_port       = 0
    to_port         = 65535
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "rds_sg" {
  vpc_id      = aws_vpc.vpc.id

  ingress {
    protocol        = "tcp"
    from_port       = 5432
    to_port         = 5432
    cidr_blocks     = ["0.0.0.0/0"]
    security_groups = [aws_security_group.ecs_sg.id]
  }

  egress {
    from_port       = 0
    to_port         = 65535
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
  }
}

data "aws_iam_policy_document" "ecs_agent" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_agent" {
  name               = "ecs-agent"
  assume_role_policy = data.aws_iam_policy_document.ecs_agent.json
}


resource "aws_iam_role_policy_attachment" "ecs_agent" {
  role       = aws_iam_role.ecs_agent.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_agent" {
  name = "ecs-agent"
  role = aws_iam_role.ecs_agent.name
}

resource "aws_launch_configuration" "ecs_launch_config" {
  image_id             = "ami-00780848600d687b6"
  iam_instance_profile = aws_iam_instance_profile.ecs_agent.name
  security_groups      = [aws_security_group.ecs_sg.id]
  user_data            = "#!/bin/bash\necho ECS_CLUSTER=my-cluster >> /etc/ecs/ecs.config"
  instance_type        = "t2.micro"
  associate_public_ip_address = true
}

resource "aws_autoscaling_group" "failure_analysis_ecs_asg" {
  name                      = "asg"
  vpc_zone_identifier       = [aws_subnet.pub_subnet.id]
  launch_configuration      = aws_launch_configuration.ecs_launch_config.name

  desired_capacity          = 1
  min_size                  = 1
  max_size                  = 1
  health_check_grace_period = 300
  health_check_type         = "EC2"
}

//resource "aws_db_subnet_group" "db_subnet_group" {
//  name       = "main"
//  subnet_ids  = [aws_subnet.pub_subnet.id, aws_subnet.pub_subnet2.id]
//  tags = {
//    Name = "My DB subnet group"
//  }
//}

resource "aws_ecr_repository" "worker" {
  name  = "worker"
}

resource "aws_ecs_cluster" "ecs_cluster" {
  name  = "my-cluster"
}


resource "aws_ecs_task_definition" "task_definition" {
  family                = "worker"
  container_definitions = jsonencode([
    {
      essential = true,
      memory = 256,
      name = "worker",
      cpu = 1,
      image = "213310144937.dkr.ecr.us-west-2.amazonaws.com/worker:latest",
      environment = []
      portMappings = [
        {
          containerPort = 443
          hostPort      = 443
        },
        {
          containerPort = 8080
          hostPort      = 8080
        },
        {
          containerPort = 80
          hostPort      = 80
        }
      ]
    }
  ])
}
//
//
resource "aws_ecs_service" "worker" {
  name            = "worker"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.task_definition.arn
  desired_count   = 2
}

output "ecr_repository_worker_endpoint" {
  value = aws_ecr_repository.worker.repository_url
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
//
//
//resource "aws_lb" "my-ecs-lb" {
//  name = "my-ecs-lb"
//  load_balancer_type = "application"
//  internal = false
//  subnets = [aws_subnet.subnet-1]
//  security_groups = [aws_security_group.lb]
//}
//
//
//resource "aws_security_group" "lb" {
//  name = "allow-all-lb"
//  vpc_id = aws_vpc.main-vpc.id
//  ingress {
//    from_port = 0
//    protocol = "-1"
//    to_port = 0
//    cidr_blocks = [0.0.0.0/0]
//  }
//  egress {
//    from_port = 0
//    protocol = "-1"
//    to_port = 0
//    cidr_blocks = [0.0.0.0/0]
//  }
//}
//
//
//
//#Create ECS
//resource "aws_ecs_service" "service" {
//  name            = "backend-service"
//  cluster = aws_ecs_cluster.web-cluster.id #This needs to be changed
//  task_definition = aws_ecs_task_definition.task-definition-test.arn
//  desired_count   = 1
//
//  #not sure if this block is required
////  ordered_placement_strategy {
////    type = "binpack"
////    field = "cpu"
////  }
//
//  load_balancer {
//    target_group_arn = aws_lb_target_group.lb_target_group.arn
//    container_name = "is590rContainer" #replace this with whatever we call our container
//    container_port = 8080 # not sure if this is the right port or not
//  }
//  launch_type = "EC2"
//  depends_on = [aws_lb_listener.web_listener] #this might can be deleted
//}
//
//
//#Create ECR
//resource "aws_ecr_repository" "is590r" {
//  name = "is590r"
//}
//
//resource "aws_ecr_lifecycle_policy" "postgrespolicy" {
//  repository = aws_ecr_repository.is590r.name
//
//  policy = <<EOF
//  {
//      "rules": [
//          {
//              "rulePriority": 1,
//              "description": "Expire JournalEntries older than 14 days",
//              "selection": {
//                  "tagStatus": "untagged",
//                  "countType": "sinceImagePushed",
//                  "countUnit": "days",
//                  "countNumber": 14
//              },
//              "action": {
//                  "type": "expire"
//              }
//          }
//      ]
//  }
//EOF
//}
//
//
//
//# Create a VPC
//resource "aws_vpc" "main-vpc" {
//  cidr_block = "10.0.0.0/16"
//  tage = {
//    Name = "Production"
//  }
//}
//
//
//#Create an Internet Gateway
//
//resource "aws_internet_gateway" "main-gateway" {
//  vpc_id = aws_vpc.main-vpc.id
//  tags = {
//    Name = "Prod-Gateway"
//  }
//}
//
//#Create a Route Table
//resource "aws_route_table" "prod-route-table"{
//  vpc_id = aws_vpc.main-vpc.id
//
//  route {
//    cidr_block = "0.0.0.0/0"
//    gateway_id = aws_internet_gateway.main-gateway.id
//  }
//
//  route {
//    ipv6_cidr_block = "::/0"
//    gateway_id = aws_internet_gateway.main-gateway.id
//  }
//
//  tags = {
//    Name = "Prod-Route_Table"
//  }
//}
//
//
//#Create a Subnet
//
//resource "aws_subnet" "subnet-1" {
//  vpc_id = aws_vpc.main-vpc.id
//  cidr_block = "10.0.1.0/24"
//  availability_zone = "us-west-2a"
//  tags = {
//    Name= "Prod-public-subnet1"
//  }
//}
//
//
//#Create another Subnet
////
//resource "aws_subnet" "subnet-1" {
//  vpc_id = aws_vpc.main-vpc.id
//  cidr_block = "10.0.200.0/24"
//  availability_zone = "us-west-2a"
//  tags = {
//    Name= "Prod-public-subnet1"
//  }
//}
//
//
//
//#Associate subnet with routing table
//resource "aws_route_table_association" "a" {
//  subnet_id = aws_subnet.subnet-1.id
//  route_table_id = aws_route_table.prod-route-table.id
//}
//
//#Create a security group
//resource "aws_security_group" "allow_web" {
//  name = "allow-web-traffic"
//  description = "Allow web inbound traffic"
//  vpc_id = aws_vpc.main-vpc.id
//
//  ingress {
//    description = "HTTPS"
//    from_port = 443
//    to_port = 443
//    protocol = "tcp"
//    cidr_blocks = [0.0.0.0/0]
//  }
//  ingress {
//    description = "HTTP"
//    from_port = 80
//    to_port = 80
//    protocol = "tcp"
//    cidr_blocks = [0.0.0.0/0]
//  }
//  ingress {
//    description = "SSH"
//    from_port = 22
//    to_port = 22
//    protocol = "tcp"
//    cidr_blocks = [0.0.0.0/0]
//  }
//  egress {
//    from_port = 0
//    to_port = 0
//    protocol = "-1"
//    cidr_blocks = [0.0.0.0/0]
//  }
//
//  tags = {
//    Name = "allow-web"
//  }
//}

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

