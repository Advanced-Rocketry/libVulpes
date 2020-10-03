import os
class Material:
    
    def __init__(self, name, color, outputs):
        self.name = name
        self.color = color
        self.outputs = outputs


materials = [Material("dilithium", 0xddcecb, ("DUST", "GEM")),
Material("iron", 0xafafaf, ("SHEET", "STICK", "DUST", "PLATE")),
Material("gold", 0xffff5d, ("DUST", "COIL", "PLATE")),
Material("silicon", 0x2c2c2b, ("INGOT", "DUST", "BOULE", "NUGGET", "PLATE")),
Material("copper", 0xd55e28, ("ORE", "COIL", "BLOCK", "STICK", "INGOT", "NUGGET", "DUST", "PLATE", "SHEET")),
Material("tin", 0xcdd5d8, ("ORE", "BLOCK", "PLATE", "INGOT", "NUGGET", "DUST")),
Material("steel", 0x55555d, ("BLOCK", "FAN", "PLATE", "INGOT", "NUGGET", "DUST", "STICK", "GEAR", "SHEET")),
Material("titanium", 0xb2669e, ("PLATE", "COIL", "INGOT", "NUGGET", "DUST", "STICK", "BLOCK", "GEAR", "SHEET")),
Material("rutile", 0xbf936a, ("ORE",)),
Material("aluminum", 0xb3e4dc, ("ORE", "COIL", "BLOCK", "INGOT", "PLATE", "SHEET", "DUST", "NUGGET", "SHEET")),
Material("iridium", 0xdedcce, ("ORE", "COIL", "BLOCK", "DUST", "INGOT", "NUGGET", "PLATE", "STICK"))]

materials = [Material("dilithium", 0xddcecb, ("DUST", "GEM")),
Material("titaniumaluminide", 0xaec2de, ("GEAR", "COIL", "BLOCK", "INGOT", "PLATE", "SHEET", "DUST", "NUGGET", "SHEET")),
Material("titaniumiridium", 0xd7dfe4, ("GEAR", "COIL", "BLOCK", "INGOT", "PLATE", "SHEET", "DUST", "NUGGET", "SHEET"))]


blockTypes = ['COIL', 'BLOCK', 'ORE']
coilTypes = ['COIL']
noIconGenTypes = ['ORE']

itemSample = '{\n    "parent": "item/generated",\n    "textures": {\n        "layer0": "libvulpes:items/@TYPE@@MATERIAL@"\n    }\n}'
blockItemSample = '{\n    "parent": "libvulpes:block/@TYPE@@MATERIAL@"\n}'

blockSample = '{\n    "parent": "minecraft:block/cube_all",\n    "textures": {\n        "all": "libvulpes:blocks/@TYPE@@MATERIAL@"\n    }\n}'
coilSample = '{\n    "parent": "libvulpes:block/tintedcubecolumn",\n    "textures": {\n        "end": "libvulpes:blocks/@TYPE@@MATERIAL@top",\n        "side": "libvulpes:blocks/@TYPE@@MATERIAL@side"\n    }\n}'

blockStateSample = '{\n    "variants": {\n        "": { "model": "libvulpes:block/@TYPE@@MATERIAL@" }\n    }\n}'


itemDir = 'src/main/resources/assets/libvulpes/models/item/'
blockDir = 'src/main/resources/assets/libvulpes/models/block/'
blockStateDir = 'src/main/resources/assets/libvulpes/blockstates/'

itemIconDir = 'src/main/resources/assets/libvulpes/textures/items/'
blockIconDir = 'src/main/resources/assets/libvulpes/textures/blocks/'

blockTagPath = "src/main/resources/data/forge/tags/blocks/"
itemTagPath = "src/main/resources/data/forge/tags/items/"

blockTagSample = '{\n  "replace": false,\n  "values": [@BLOCKLIST@]\n}'


def getMatrix(color):
    r = ((color >> 16) & 0xff)/0xff
    g = ((color >> 8) & 0xff)/0xff
    b = (color & 0xff)/0xff
    
    return str(r) + ' 0 0 0 ' + str(g) + ' 0 0 0 ' + str(b)

def getCommand(inputFile, outputFile, color):
    return 'convert ' + inputFile + ' -color-matrix "' + getMatrix(color) + '" ' + outputFile

def genItem(mat, objType):
    if not objType in blockTypes:
        output = itemSample.replace('@MATERIAL@', mat.name).replace('@TYPE@', objType.lower())
    else:
        output = blockItemSample.replace('@MATERIAL@', mat.name).replace('@TYPE@', objType.lower())
    filename = itemDir + objType.lower() + mat.name + '.json'
    f = open(filename, 'w')
    f.write(output)
    
    # generate the icon now
    if not objType in blockTypes:
        inputFile = itemIconDir + objType.lower() + '.png'
        outputFile = itemIconDir + objType.lower() + mat.name + '.png'
        cmd = getCommand(inputFile, outputFile, mat.color)
        os.system(cmd)

def genBlock(mat, objType):
    if objType in coilTypes:
        output = coilSample.replace('@MATERIAL@', mat.name).replace('@TYPE@', objType.lower())
    else:
        output = blockSample.replace('@MATERIAL@', mat.name).replace('@TYPE@', objType.lower())
    
    filename = blockDir + objType.lower() + mat.name + '.json'
    f = open(filename, 'w')
    f.write(output)
    
    # generate the blockState
    output = blockStateSample.replace('@MATERIAL@', mat.name).replace('@TYPE@', objType.lower())
    filename = blockStateDir + objType.lower() + mat.name + '.json'
    f = open(filename, 'w')
    f.write(output)
    
    # Generate the icon now
    if not objType in noIconGenTypes:
        if objType in coilTypes:
            inputFile = blockIconDir + objType.lower() + 'pole.png'
            outputFile = blockIconDir + objType.lower() + mat.name + 'top.png'
            cmd = getCommand(inputFile, outputFile, mat.color)
            os.system(cmd)
            inputFile = blockIconDir + objType.lower() + 'side.png'
            outputFile = blockIconDir + objType.lower() + mat.name + 'side.png'
            cmd = getCommand(inputFile, outputFile, mat.color)
            os.system(cmd)
        else:
            inputFile = blockIconDir + objType.lower() + '.png'
            outputFile = blockIconDir + objType.lower() + mat.name + '.png'
            cmd = getCommand(inputFile, outputFile, mat.color)
            os.system(cmd)

def printEnLang(mat, objType, block):
    human_mat = mat.name
    human_type = objType.lower()
    human_mat = human_mat[0].upper() + human_mat[1:]
    human_type = human_type[0].upper() + human_type[1:]
    
    if block:
        print('    "block.libvulpes.{}{}": "{} {}",'.format(objType.lower(),mat.name, human_mat, human_type))
    else:
        print('    "item.libvulpes.{}{}": "{} {}",'.format(objType.lower(),mat.name, human_mat, human_type))

def generateTag(tagPath, mat, objType):
    if not os.path.exists(tagPath + objType.lower()):
        os.makedirs(tagPath + objType.lower())
    filename = tagPath + objType.lower() + '/' + mat.name + '.json'
    
    contents = blockTagSample.replace('@BLOCKLIST@', '    "libvulpes:' + objType.lower() + mat.name + '"')
    
    f = open(filename, 'w')
    f.write(contents)
    f.close()

objTypeToMaterialMap = {}

for mat in materials:
    for objType in mat.outputs:
        if objType not in objTypeToMaterialMap:
            objTypeToMaterialMap[objType] = []
        #objTypeToMaterialMap[objType].append(mat)
        
        #genItem(mat, objType)
        
        if objType in blockTypes:
        #    genBlock(mat, objType)
            generateTag(blockTagPath, mat, objType)
        generateTag(itemTagPath, mat, objType)
        #printEnLang(mat, objType, objType in blockTypes)


for objType in objTypeToMaterialMap.keys():
    
    contentString = []
    for mat in objTypeToMaterialMap[objType]:
        contentString.append('    "libvulpes:' + objType.lower() + mat.name + '"')
    
    contents = blockTagSample.replace('@BLOCKLIST@', ',\n'.join(contentString))
    f = None
    try:
        if objType in blockTypes:
            f = open(blockTagPath + objType.lower() + '.json', 'w')
        else:
            f = open(itemTagPath + objType.lower() + '.json', 'w')
        f.write(contents)
        f.close()
    except FileNotFoundError:
        pass

    
