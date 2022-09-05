#!/bin/ash

echo "Loading host server with game ${GAME} in version ${VERSION}"

# Copy game data content to container workdir
echo "Copy game data content to container workdir"
cp -r /home/data/* /home/container/
# Copy game version content to minecraft plugins directory
echo "Copy version plugins into container plugins directory"
cp -r /data/"$GAME"/"$VERSION"/plugins/* /home/minecraft/plugins/
cd /home/container || echo "Container error, workdir not found"

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

# Ensure MIN_MEMORY & MAX_MEMORY are defined
if ! set | grep '^MIN_MEMORY=' >/dev/null 2>&1; then
    MIN_MEMORY=2048M
elif [ -z "${MIN_MEMORY}" ]; then
    MIN_MEMORY=2048M
fi

if ! set | grep '^MAX_MEMORY=' >/dev/null 2>&1; then
    MAX_MEMORY=2048M
elif [ -z "${MAX_MEMORY}" ]; then
    MAX_MEMORY=2048M
fi

# Ensure STARTUP is defined
if ! set | grep '^STARTUP=' >/dev/null 2>&1; then
    STARTUP="java -Xms${MIN_MEMORY} -Xmx${MAX_MEMORY} -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -Duser.timezone=Europe/Paris -DIReallyKnowWhatIAmDoingISwear -Dfile.encoding=UTF-8 -jar paper*.jar nogui"
elif [ -z "${STARTUP}" ]; then
    STARTUP="java -Xms${MIN_MEMORY} -Xmx${MAX_MEMORY} -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -Duser.timezone=Europe/Paris -DIReallyKnowWhatIAmDoingISwear -Dfile.encoding=UTF-8 -jar paper*.jar nogui"
fi

echo "Min memory: ${MIN_MEMORY}"
echo "Max memory: ${MAX_MEMORY}"

# Output Current Java Version
java -version

# Replace Startup Variables
MODIFIED_STARTUP=$(eval echo "$(echo "${STARTUP}" | sed -e 's/{{/${/g' -e 's/}}/}/g')")
echo "${MODIFIED_STARTUP}"

# Run the Server
${MODIFIED_STARTUP}