def assemble():
    """
    Program that converts assembly instructions
    into machine code to be loaded into logisim
    RAM. Based on the v2.0 raw file format.
    Author: Kwasi Buansi
    Time spent: 6 hours
    """

    from pathlib import Path
    import sys

    input_filename = input("Enter input filename: ")

    try:
        with open(input_filename, 'r') as input_file:
            output_filename = input("Enter output filename: ")
            output_path = Path(output_filename)

            opcodes = {
                "INPUT" : "04",
                "OUTPUT" : "07",
                "JMP" : "0A",
                "LOAD" : "0E",
                "INC" : "14",
                "MOV" : "17",
                "ADD" : "1A",
                "HALT" : "1D",
                "NOP" : "1E"
            }

            current_output = ["v2.0 raw\n"]
            for line_number, line in enumerate(input_file, start=1):
                words = line.split()
                if len(words) == 0:
                    continue
                line_opcode = words[0].upper()
                if len(words) == 1 and line_opcode in opcodes and line_opcode not in ["JMP", "LOAD"]: #Reads an opcode that isn't JMP or LOAD
                    current_output.append(opcodes[line_opcode] + "\n")
                elif len(words) == 2 and line_opcode in ["JMP", "LOAD"]: #Reads JMP, LOAD, assuming it has a parameter in the correct format
                    current_output.append(opcodes[line_opcode]+ "\n")
                    current_output.append("{:02x}".format(int(words[1], 16))+"\n") #Convert to 2 bit hex
                else: #Illegal input
                    raise ValueError(line_number)
                with open(output_path, 'w') as output_file:
                    output_file.writelines(current_output)
    except FileNotFoundError:
        print("Could not find file")
        sys.exit()
    except ValueError as error_line:
        print("Syntax error " + str(error_line))

if __name__ == "__main__":
    assemble()