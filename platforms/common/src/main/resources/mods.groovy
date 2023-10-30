ModsDotGroovy.make {
    def props = buildProperties

    license = props.MOD_LICENSE
    issueTrackerUrl = 'https://discord.apexstudios.dev/'

    onForge {
        modLoader = 'javafml'
        loaderVersion = "[${props.MCFORGE_LOADER_VERSION},)"
    }

    onFabric {
        sourcesUrl = "https://github.com/ApexStudios-Dev/${props.MOD_NAME}"

        custom = [
                modmenu: [
                        links: [
                                'modmenu.discord': 'https://discord.apexstudios.dev'
                        ],
                        update_checker: true
                ]
        ]
    }

    mod {
        modId = props.MOD_ID
        displayName = props.MOD_NAME
        version = this.version
        updateJsonUrl = "https://api.modrinth.com/updates/${props.MOD_ID}/forge_updates.json"
        displayUrl = "https://apexstudios.dev/${props.MOD_ID}"
        description = props.MOD_DESCRIPTION

        contributor('Apex', 'Founder, Programmer')
        contributor('FantasyGaming', 'Co-Founder, Artist')
        contributor('RudySPG', 'Web-Developer, Supporter, Beta Tester')
        contributor('TobiSPG', 'Beta Tester')

        onFabric {
            logoFile = 'logo.png'

            author('ApexStudios')

            dependencies {
                minecraft {
                    versionRange = props.MINECRAFT_FABRIC_VERSION_RANGE
                }

                mod(props.APEXCORE_ID) {
                    versionRange = props.APEXCORE_VERSION_FABRIC_RANGE
                }
            }

            entrypoints {
                setMain("${props.MOD_GROUP}.fabric.entrypoint.${props.MOD_NAME}ModInitializer")
                setClient("${props.MOD_GROUP}.fabric.entrypoint.${props.MOD_NAME}ClientModInitializer")
                // .toString() cause does not like GString
                entrypoint('fabric-datagen', "${props.MOD_GROUP}.fabric.entrypoint.${props.MOD_NAME}ModInitializer".toString())
            }
        }

        onForge {
            credits = 'ApexStudios'
            displayTest = DisplayTest.MATCH_VERSION
            logoFile = 'banner.png'

            dependencies {
                minecraft {
                    versionRange = props.MINECRAFT_FORGE_VERSION_RANGE
                }

                forge {
                    versionRange = props.MCFORGE_VERSION_RANGE
                }

                mod(props.APEXCORE_ID) {
                    versionRange = props.APEXCORE_VERSION_FORGE_RANGE
                }
            }
        }
    }
}