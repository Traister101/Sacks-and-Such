def langify(string: str) -> str:
    return ' '.join([word.capitalize() for word in string.split('_')])
