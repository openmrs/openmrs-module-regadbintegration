# Variables
$postgresql_postgres_password = "regadb_password"
$postgresql_regadb_user = "regadb_user"
$postgresql_regadb_password = "regadb_password"
$postgresql_regadb_database_name = "regadb"

# Defaults for exec
Exec {
	path => ["/bin", "/usr/bin", "/usr/local/bin", "/usr/local/sbin", "/sbin", "/usr/sbin"],
	user => "root"
}

# Download artifacts
exec { "fetch-regadb-war":
	command => "wget -P /regadb/artifacts/ http://rega.kuleuven.be/cev/regadb/download/files/regadb.war",
	creates => "/regadb/artifacts/regadb.war",
	timeout => 0,
}

exec { "fetch-regadb-schema":
	command => "wget -P /regadb/artifacts/ http://rega.kuleuven.be/cev/regadb/download/files/postgresschema.sql",
	creates => "/regadb/artifacts/postgresschema.sql",
	timeout => 0,
}

# Setup /etc directories and config
file { "/etc/rega_institute":
    ensure 	=> "directory",
    owner	=> "tomcat6",
	group 	=> "tomcat6",
	require	=> Package["tomcat6"]
}

file { "/etc/rega_institute/regadb":
    ensure 	=> "directory",
    owner	=> "tomcat6",
	group 	=> "tomcat6",
	require	=> File["/etc/rega_institute"]
}

file { "/etc/rega_institute/regadb/log":
    ensure 	=> "directory",
    owner	=> "tomcat6",
	group 	=> "tomcat6",
	require	=> File["/etc/rega_institute/regadb"]
}

file { "/etc/rega_institute/regadb/query":
    ensure 	=> "directory",
    owner	=> "tomcat6",
	group 	=> "tomcat6",
	require	=> File["/etc/rega_institute/regadb"]
}

file { "/etc/rega_institute/regadb/import":
    ensure 	=> "directory",
    owner	=> "tomcat6",
	group 	=> "tomcat6",
	require	=> File["/etc/rega_institute/regadb"]
}

file { "/etc/rega_institute/regadb/sequencedb":
    ensure 	=> "directory",
    owner	=> "tomcat6",
	group 	=> "tomcat6",
	require	=> File["/etc/rega_institute/regadb"]
}

file { "/etc/rega_institute/regadb/global-conf.xml":
	ensure => "present",
	source => "/regadb/artifacts/global-conf.xml",
	require	=> File["/etc/rega_institute/regadb"]
}

# Kernel memory settings
$postgres_sysctl_settings = {
	"kernel.shmmax"  => { value => 268435456 },
	"net.core.rmem_max"  => { value =>  524288 },
  	"net.core.wmem_max"  => { value =>  524288 }
}

# Make sure that we tweak the kernel before installing Postgresql
$postgres_sysctl_dependency = {
  before => Class["postgresql::server"]
}

# Generate the sysctl resources
create_resources(sysctl::value, $postgres_sysctl_settings, $postgres_sysctl_dependency)

# Install Postgresql server
class { "postgresql::server":
  postgres_password	=> $postgresql_postgres_password
}

# Postgres configuration tweaks for the regadb
$postgres_server_conf ={
	"shared_buffers" => { value => "64MB" },
	"work_mem" => { value => "8MB" },
	"maintenance_work_mem" => { value => "32MB" },
	"effective_cache_size" => { value => "256MB" },
	"checkpoint_segments" => { value => "8" },
	"checkpoint_completion_target" => { value => "0.8"}
}

# Generate postgres configuration resources
create_resources(postgresql::server::config_entry, $postgres_server_conf)

# Create regadb database
postgresql::server::db { $postgresql_regadb_database_name:
	user     => $postgresql_regadb_user,
	password => $postgresql_regadb_password,
	before => Exec["import-regadb-schema"]
}

# Import regadb database schema
exec { "import-regadb-schema":
	cwd	=> "/regadb/artifacts",
	environment => [ "PGPASSWORD=$postgresql_regadb_password" ],
	command	=> "psql -U ${postgresql_regadb_user} -h localhost ${postgresql_regadb_database_name} < postgresschema.sql",
	require => Exec["fetch-regadb-schema"]
}

# Install Tomcat
package { "tomcat6": 
	ensure	=> latest,
	require => Exec["import-regadb-schema"]
}

# Define Tomcat service
service { "tomcat6":
    ensure  => "running",
    enable  => "true",
    require => Package["tomcat6"]
}

# Deploy regadb WAR file
file { "/var/lib/tomcat6/webapps/regadb.war":
	source	=> "/regadb/artifacts/regadb.war",
	owner	=> "tomcat6",
	group 	=> "tomcat6",
	require => [ Package["tomcat6"], Exec["fetch-regadb-war"] ],
	notify  => Service["tomcat6"],
}
