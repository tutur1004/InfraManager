#!/bin/ash

# Copy game data content to container workdir
echo "Copy game data content to container workdir"
cp -r /home/data/* /home/container/
cd /home/container

# Replace env values in config and .properties files 
# Replace %{KEY}% with their values
echo "Replace env values in files"
export IFS=";"
d=$'\03'
for config in $CONFIGPARSER; do
	if echo "$config" | grep -q "="
	then
		KEY="$(echo "$config" | cut -d'=' -f1)"
		VAL="$(echo "$config" | cut -d'=' -f2)"
		find * -type f \( -name "*.yml" -o -name "*.properties" \) -exec sed -i "s${d}%{$KEY}%${d}$VAL${d}g" {} +
	fi
done
unset IFS

# Output Current Java Version
java -version

# Replace Startup Variables
MODIFIED_STARTUP=`eval echo $(echo ${STARTUP} | sed -e 's/{{/${/g' -e 's/}}/}/g')`
echo "${MODIFIED_STARTUP}"

# Run the Server
${MODIFIED_STARTUP}